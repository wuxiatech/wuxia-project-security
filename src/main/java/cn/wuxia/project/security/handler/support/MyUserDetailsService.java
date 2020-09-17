package cn.wuxia.project.security.handler.support;

import cn.wuxia.common.spring.SpringContextHolder;
import cn.wuxia.common.util.ClassLoaderUtil;
import cn.wuxia.common.util.ServletUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.common.security.UserContext;
import cn.wuxia.project.common.security.UserContextUtil;
import cn.wuxia.project.security.common.MyNobindedUserDetails;
import cn.wuxia.project.security.common.MyUserDetails;
import cn.wuxia.project.security.handler.bean.UserLoginedData;
import cn.wuxia.project.security.handler.bean.WxAuthUserInfoBean;
import cn.wuxia.project.security.handler.service.UserLoginedService;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * implements SpringSecurity's UserDetailsService interface, get customer user
 * info Detail.
 *
 * @author songlin.li
 */
@Transactional(readOnly = true)
public class MyUserDetailsService implements UserDetailsService {
    public static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Value("#{'${cas.userLoginedService:null}'}")
    private String loginedServiceBeanName;

    protected UserLoginedService userLoginedService;

    @Autowired
    private HttpServletRequest request;
    private static final String NON_EXISTENT_PASSWORD_VALUE = "NO_PASSWORD";
    boolean enabled = true;
    boolean accountNonExpired = true;
    boolean credentialsNonExpired = true;
    boolean accountNonLocked = true;

    /**
     * loadUserByUsername
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {
        logger.info("################################### UserName:[" + userName + "]");
        UserLoginedData loginedUserData = null;
        try {
            loginedUserData = userLoginedService.loginByAccountname(userName);
        } catch (Exception e) {
            logger.error("", e);
            throw new UsernameNotFoundException("error.userNotExist", e);
        }
        if (loginedUserData == null) {
            throw new UsernameNotFoundException("用户" + userName + "不存在或无权访问！");
        }
        final Set<GrantedAuthority> grantedAuthorities = Sets.newHashSet();
        if (StringUtil.isNotBlank(loginedUserData.getPermissionNames())) {
            for (String permissionName : StringUtil.split(loginedUserData.getPermissionNames(), ",")) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permissionName));
            }
        }
        MyUserDetails userDetail = new MyUserDetails(userName, NON_EXISTENT_PASSWORD_VALUE, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                grantedAuthorities);
        userDetail.setClientIp(ServletUtils.getRemoteIP(request));
        logger.info("################################### UserName:[" + loginedUserData.getRealName() + "] has roles：{"
                + grantedAuthorities.toString() + "}");
        UserContext userContext = new UserContext(userDetail.getUid(), userDetail.getDisplayName(), userDetail.getMobile());
        UserContextUtil.saveUserContext(userContext);
        return getUserLoginedService().afterLogin(loginedUserData, userDetail);

    }

    private UserDetails authFromWechat(String userName, WxAuthUserInfoBean wxAuthUserInfoBean) {
        MyUserDetails userDetail = null;
        UserLoginedData loginedUserData = null;
        if (StringUtil.isNotBlank(wxAuthUserInfoBean.getUnionid())) {
            logger.debug("根据参数unionid={}查找用户", wxAuthUserInfoBean.getUnionid());
            loginedUserData = getUserLoginedService().loginByWxUnionid(wxAuthUserInfoBean.getAppid(), wxAuthUserInfoBean.getUnionid());
        }
        if (loginedUserData == null && StringUtil.isNotBlank(wxAuthUserInfoBean.getOpenid())) {
            logger.debug("根据参数openid={}查找用户", wxAuthUserInfoBean.getOpenid());
            loginedUserData = getUserLoginedService().loginByWxOpenid(wxAuthUserInfoBean.getAppid(), wxAuthUserInfoBean.getOpenid());
        }

        if (loginedUserData == null) {
            throw new UsernameNotFoundException("用户" + wxAuthUserInfoBean.getNickname() + "不存在或无权访问！");
        } else if (StringUtil.isBlank(loginedUserData.getId())) {
            userDetail = new MyNobindedUserDetails(userName, NON_EXISTENT_PASSWORD_VALUE, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                    new ArrayList<GrantedAuthority>(0));
            ((MyNobindedUserDetails) userDetail).setAppid(wxAuthUserInfoBean.getAppid());
            ((MyNobindedUserDetails) userDetail).setAvatar(wxAuthUserInfoBean.getHeadimgurl());
            ((MyNobindedUserDetails) userDetail).setOpenid(wxAuthUserInfoBean.getOpenid());
            ((MyNobindedUserDetails) userDetail).setUnionid(wxAuthUserInfoBean.getUnionid());
            ((MyNobindedUserDetails) userDetail).setDisplayName(wxAuthUserInfoBean.getNickname());
        } else {
            final List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(0);
            if (StringUtil.isNotBlank(loginedUserData.getPermissionNames())) {
                for (String permissionName : StringUtil.split(loginedUserData.getPermissionNames(), ",")) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(permissionName));
                }
            }
            userDetail = new MyUserDetails(userName, NON_EXISTENT_PASSWORD_VALUE, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                    grantedAuthorities);
            logger.info("##################### UserName:[{}] has roles：{}", userName, grantedAuthorities.toString());
            UserContext userContext = new UserContext(userDetail.getUid(), userDetail.getDisplayName(), userDetail.getMobile());
            UserContextUtil.saveUserContext(userContext);
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        userDetail.setClientIp(ServletUtils.getRemoteIP(request));
        return getUserLoginedService().afterLogin(loginedUserData, userDetail);
    }

    public UserLoginedService getUserLoginedService() {
        if (userLoginedService == null) {
            if (StringUtil.isNotBlank(loginedServiceBeanName)) {
                if (StringUtil.indexOf(loginedServiceBeanName, ".") > 0) {
                    Class loginedServiceClass = ClassLoaderUtil.loadClass(loginedServiceBeanName);
                    this.userLoginedService = (UserLoginedService) SpringContextHolder.getBean(loginedServiceClass);
                } else {
                    this.userLoginedService = SpringContextHolder.getBean(loginedServiceBeanName);
                }
            } else {
                this.userLoginedService = SpringContextHolder.getBean(UserLoginedService.class);
            }
        }
        return userLoginedService;
    }

    public void setUserLoginedService(UserLoginedService userLoginedService) {
        this.userLoginedService = userLoginedService;
    }

    public void setLoginedServiceBeanName(String loginedServiceBeanName) {
        this.loginedServiceBeanName = loginedServiceBeanName;
    }


}
