package cn.wuxia.project.security.handler.support;

import cn.wuxia.common.util.StringUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    private String logoutSuccessUrl;

    private String logoutUrl;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException,
            ServletException {
        /**
         * remove memcached handler context
         */
        request.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
        request.getSession().invalidate();
        //        String[] keys = StringUtil.getTemplateKey(logoutSuccessUrl);
        //        for (String key : keys) {
        //            String value = PropertiesUtils.getPropertiesValue(key);
        //            logoutSuccessUrl = StringUtil.replaceKeysSimple(logoutSuccessUrl, key, value);
        //        }
        String httpPath = request.getServerPort() == 443 ? "https" : request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            httpPath = httpPath + ":" + request.getServerPort();
        }
        String service = StringUtil.indexOf(logoutSuccessUrl, "/") == 0 ? httpPath
                + URLEncoder.encode(logoutSuccessUrl, "ISO-8859-1") : URLEncoder.encode(logoutSuccessUrl, "ISO-8859-1");
        response.sendRedirect(logoutUrl + "?service=" + service);
    }

    public void setLogoutSuccessUrl(String logoutSuccessUrl) {
        this.logoutSuccessUrl = logoutSuccessUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

}
