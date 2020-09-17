/*
 * Created on :2013年7月23日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.handler.support;

import cn.wuxia.common.exception.AppPermissionException;
import cn.wuxia.project.common.support.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;



@SuppressWarnings("deprecation")
public class MyLoginUrlEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(MyLoginUrlEntryPoint.class);

    public MyLoginUrlEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException,
            ServletException {
        StringBuffer url = request.getRequestURL();
        String callback = request.getHeader("Referer");
        //        AppPermissionException errorMessage = new AppPermissionException("您需要登录之后才能继续操作");
        AppPermissionException errorMessage = new AppPermissionException();
        logger.info("您需要登录之后才能继续操作");
        // 如果是ajax请求响应头会有，x-requested-with；
        if (StringUtils.equalsIgnoreCase(request.getHeader("x-requested-with"), "XMLHttpRequest")
                || StringUtils.equalsIgnoreCase(request.getHeader("X-Requested-With"), "XMLHttpRequest")) {
            // 在响应头设置session状态
            response.addHeader("sessionstatus", "timeout");
            response.setStatus(999);
            response.addHeader("Referer", URLEncoder.encode(callback, Constants.UTF_8));
            // response.sendRedirect();
            // throw new AppPermissionException();
            request.setAttribute("javax.servlet.error.exception", errorMessage);
            throw errorMessage;
//            String forwarPage = StringUtils.substringAfter(Constants.FORWARD_COMMON_403, ":");
//            RequestDispatcher dispatcher = request.getRequestDispatcher(forwarPage);
//            dispatcher.forward(request, response);
        }
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
        String rUrl = buildRedirectUrlToLoginPage(request, response, authException);
        super.commence(request, response, authException);
    }
}
