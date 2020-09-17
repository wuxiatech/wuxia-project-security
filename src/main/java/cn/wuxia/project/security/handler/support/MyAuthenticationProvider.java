/*
 * Created on :2013-6-21 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.handler.support;

import cn.wuxia.project.common.support.Constants;
import cn.wuxia.project.security.common.MyPasswordEncoder;
import cn.wuxia.project.security.common.MyUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class MyAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private UserDetailsService userDetailsService;

    private MyPasswordEncoder myPasswordEncoder;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setMyPasswordEncoder(MyPasswordEncoder myPasswordEncoder) {
        this.myPasswordEncoder = myPasswordEncoder;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        Locale locale = LocaleContextHolder.getLocale();
        String inputPassword = (String) authentication.getCredentials();

        // 验证加密后的密码
        boolean isValid = myPasswordEncoder.isPasswordValid(userDetails.getPassword(), inputPassword, null);
        if (!isValid) {
            setCountToSession();
            String error = messages.getMessage("error.passwordError", locale);
            throw new AuthenticationServiceException(error);
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Integer count = (Integer) request.getSession().getAttribute(Constants.LOGIN_SEESION_COUNT);

        request.getSession().setAttribute(Constants.LOGIN_SESSION_NAME, username); //保存登录用户名
        if (count != null && count >= 3) {
            String verifycode = request.getParameter("_spring_security_bite_me");
//            Captcha cap = (Captcha) request.getSession().getAttribute("login_captcha");
//            boolean checkResult = false;
//            if (cap != null && StringUtil.isNotBlank(verifycode)) {
//                checkResult = StringUtil.equalsIgnoreCase(cap.getAnswer(), verifycode);
//                if (checkResult == false) {
//                    throw new AuthenticationServiceException("验证码错误！");
//                }
//            }
        }
        Locale locale = LocaleContextHolder.getLocale();
        MyUserDetails user = null;
        try {
            user = (MyUserDetails) userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException notFound) {
            setCountToSession();
            String message = messages.getMessage(notFound.getMessage(), locale);
            throw new AuthenticationServiceException(message);
        } catch (AuthenticationException repositoryProblem) {
            String message = messages.getMessage(repositoryProblem.getMessage(), locale);
            throw new AuthenticationServiceException(message);
        }
        // if (!user.getVistorIsEmailVerify()) {
        // throw new AuthenticationServiceException("emailNotVerify#" +
        // user.getVistorEmail());
        // }
        return user;
    }

    private void setCountToSession() {
        Integer count = (Integer) request.getSession().getAttribute(Constants.LOGIN_SEESION_COUNT);
        if (count != null) {
            request.getSession().setAttribute(Constants.LOGIN_SEESION_COUNT, count + 1);
        } else {
            request.getSession().setAttribute(Constants.LOGIN_SEESION_COUNT, 1);
        }
    }
}
