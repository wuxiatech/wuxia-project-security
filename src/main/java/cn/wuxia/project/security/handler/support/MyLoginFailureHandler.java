/*
 * Created on :2013-6-28 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.handler.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.google.common.collect.Maps;

import cn.wuxia.project.common.support.Constants;
import cn.wuxia.common.util.JsonUtil;

/**
 * 验证失败处理，增加ajax错误处理。
 * 
 * @author songlin.li
 * @version
 */
public class MyLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        if (StringUtils.equalsIgnoreCase("XMLHttpRequest", request.getHeader("x-requested-with"))
                || StringUtils.equalsIgnoreCase("XMLHttpRequest", request.getHeader("X-Requested-With"))) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("success", false);
            map.put("errorMsg", exception.getMessage());
            map.put("seesionCount", (Integer) request.getSession().getAttribute(Constants.LOGIN_SEESION_COUNT));
            String contentType = "application/json";
            response.setContentType(contentType);
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(JsonUtil.toJson(map));
            out.flush();
            out.close();
            return;
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
