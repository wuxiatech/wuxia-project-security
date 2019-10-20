/**
 *
 */
package cn.wuxia.project.security.handler.support;

import cn.wuxia.common.spring.SpringContextHolder;
import cn.wuxia.common.util.ArrayUtil;
import cn.wuxia.common.util.ClassLoaderUtil;
import cn.wuxia.common.util.ServletUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.project.basic.core.conf.support.DTools;
import cn.wuxia.project.common.security.UserContext;
import cn.wuxia.project.common.security.UserContextUtil;
import cn.wuxia.project.common.support.Constants;
import cn.wuxia.project.security.common.MyNobindedUserDetails;
import cn.wuxia.project.security.common.MyUserDetails;
import cn.wuxia.project.security.handler.bean.UserLoginedData;
import cn.wuxia.project.security.handler.bean.WxAuthUserInfoBean;
import cn.wuxia.project.security.handler.service.UserLoginedService;
import org.apache.commons.collections4.MapUtils;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * [ticket id]
 * 登录成功后的信息获取
 *
 * @author songlin
 * @ Version : V<Ver.No> <14 May, 2015>
 */
public class MyCasUserDetailsService extends AbstractCasAssertionUserDetailsService {

    protected static final Logger logger = LoggerFactory.getLogger(MyCasUserDetailsService.class);

    @Value("#{'${cas.userLoginedService:null}'}")
    private String loginedServiceBeanName;

    protected UserLoginedService userLoginedService;

    private static final String NON_EXISTENT_PASSWORD_VALUE = "NO_PASSWORD";
    boolean enabled = true;
    boolean accountNonExpired = true;
    boolean credentialsNonExpired = true;
    boolean accountNonLocked = true;
    private String[] attributes;

    public MyCasUserDetailsService(final String... attributes) {
        Assert.notNull(attributes, "attributes cannot be null.");
        Assert.isTrue(attributes.length > 0, "At least one attribute is required to retrieve roles from.");
        this.attributes = attributes;
    }

    /**
     * 必须是https才允许获取到cas返回的自定义数据
     * @param assertion
     * @return
     */
    @Override
    protected UserDetails loadUserDetails(final Assertion assertion) {
        Map<String, Object> returnAttrs = assertion.getPrincipal().getAttributes();
        for (Map.Entry<String, Object> attribute : returnAttrs.entrySet()) {
            logger.debug("cas return key:{} value:{}", attribute.getKey(), attribute.getValue());
        }
        String userName = assertion.getPrincipal().getName();
        logger.info("登录用户名:{}", userName);


        /**
         * 处理微信登录
         */
        if (MapUtils.isNotEmpty(returnAttrs) && StringUtil.equals(MapUtils.getString(returnAttrs, "credentialType"), "WeChatCredential")) {
            WxAuthUserInfoBean wxAuthUserInfoBean = BeanUtil.mapToBean(returnAttrs, WxAuthUserInfoBean.class);
            if (wxAuthUserInfoBean != null) {
                return authFromWechat(userName, wxAuthUserInfoBean);
            }
        }
        /**
         * cas 返回的值有, credentialType=MyUsernamePasswordCredential
         */
        String wxappid = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            String platform = request.getHeader(Constants.HEADER_PLATFORM_NAME);
            if (StringUtil.isNotBlank(platform)) {
                wxappid = DTools.dic(platform);
            }
            logger.info("platform={}, wxappid={}", platform, wxappid);
        }
        Object[] queryvalues = new Object[]{};
        MyUserDetails userDetail = null;
        for (String attribute : attributes) {
            queryvalues = ArrayUtil.add(queryvalues, returnAttrs.get(attribute));
        }

        UserLoginedData loginedUserData = null;
        if (ArrayUtil.isNotEmpty(queryvalues) && MapUtils.isNotEmpty(returnAttrs) && queryvalues[0] != null) {
            logger.debug("根据参数{}查找用户", queryvalues[0]);
            loginedUserData = getUserLoginedService().loginByCasUserid((String) queryvalues[0]);
        }
        if (loginedUserData == null) {
            logger.debug("根据参数{}查找用户", userName);
            loginedUserData = getUserLoginedService().loginByAccountname(userName);
        }

        if (loginedUserData == null) {
            throw new UsernameNotFoundException("用户" + userName + "不存在或无权访问！");
        }
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        if (StringUtil.isNotBlank(loginedUserData.getPermissionNames())) {
            for (String permissionName : StringUtil.split(loginedUserData.getPermissionNames(), ",")) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permissionName));
            }
        }
        userDetail = new MyUserDetails(userName, NON_EXISTENT_PASSWORD_VALUE, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
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

    public void setAttributes(String... attributes) {
        this.attributes = attributes;
    }
}
