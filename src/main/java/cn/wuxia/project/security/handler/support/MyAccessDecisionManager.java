package cn.wuxia.project.security.handler.support;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import cn.wuxia.common.spring.support.MessageSourceHandler;
import cn.wuxia.common.util.ListUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.security.core.enums.LoginResourceType;
import cn.wuxia.project.security.common.MyUserDetails;

public class MyAccessDecisionManager implements AccessDecisionManager {
    private static final Logger logger = LoggerFactory.getLogger(MyAccessDecisionManager.class);

    private MessageSourceHandler messageSourceHandler;

    // In this method, need to compare authentication with configAttributes.
    // 1, A object is a URL, a filter was find permission configuration by this
    // URL, and pass to here.
    // 2, Check authentication has attribute in permission configuration
    // (configAttributes)
    // 3, If not match corresponding authentication, throw a
    // AccessDeniedException.
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        //如果当前url没有任何权限规则判断，则忽略
        if (ListUtil.isEmpty(configAttributes))
            return;


        //判断url权限规则与当前用户拥有的规则对比，如果用户拥有改uri权限，则放行
        for (ConfigAttribute configAttribute : configAttributes) {
            // To access the resources needed for the limits of authority
            String needPermission = configAttribute.getAttribute();
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                // Users have access authentication
                if (StringUtil.equals(needPermission, ga.getAuthority())) {
                    return;
                }
            }
        }
        //如果当前url不需要登录，则忽略
        if (configAttributes.contains(new SecurityConfig(LoginResourceType.NOT_NEED_PC_LOGIN.name()))
                || configAttributes.contains(new SecurityConfig(LoginResourceType.NOT_NEED_WX_LOGIN.name()))
                ) {
            return;
        }
        /**
         * 如果当前url需要微信登录，暂不在此处理，在{@link OauthInterceptor}中处理
         */
        if (configAttributes.contains(new SecurityConfig(LoginResourceType.NEED_WX_LOGIN.name()))
                && !configAttributes.contains(new SecurityConfig(LoginResourceType.NEED_PC_LOGIN.name()))) {
            return;
        }

        //来到此处则表示没有权限访问，则需要登录或者赋权
        Object detail = authentication.getPrincipal();
        String errorMessage = "你没有权限访问";
        if (getMessageSourceHandler() != null)
            errorMessage = getMessageSourceHandler().getString("error.noAccessPermission", LocaleContextHolder.getLocale());
        String warnlog = "---------------------{} 访问当前路径需要权限 {" + configAttributes + "}";
        if ("anonymousUser".equals(detail)) {
            warnlog += ", 但当前用户尚未登录";
        } else if (detail instanceof User) {
            warnlog += ", 但当前用户[" + ((MyUserDetails) detail).getUsername() + "]的权限为{" + authentication.getAuthorities() + "}";
        }
        logger.warn(warnlog, object);
        throw new AccessDeniedException(errorMessage);
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }


    public void setMessageSourceHandler(MessageSourceHandler messageSourceHandler) {
        this.messageSourceHandler = messageSourceHandler;
    }

    public MessageSourceHandler getMessageSourceHandler() {
        return messageSourceHandler;
    }
}
