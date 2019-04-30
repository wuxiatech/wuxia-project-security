/*
 * Created on :2013-6-28 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.handler.support;

import cn.wuxia.project.security.common.MyUserDetails;
import cn.wuxia.project.common.support.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 验证成功处理，增加ajax验证处理。
 *
 * @author songlin.li
 */
public class MyLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//    @Autowired
//    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        /**
         * 提供给/druid/ session监控到用户名
         */
        session.setAttribute("druid_session_user", userDetails != null ? userDetails.getUid() : "");
        /**
         * when success then log the login history
         */
//        UserOperationHistory userOperationHistory = new UserOperationHistory();
//        userOperationHistory.setOperationUserId(userDetails.getVisitorId());
//        String ip = request.getRemoteAddr();
//
//        userOperationHistory.setOperationType(UserOperationEnum.DENGLU.getType());
//        userOperationHistory.setOperationIp(ip);
//        userService.addUserOpertionHistory(userOperationHistory);
        /**
         * session to memcached, spring handler save user info in local thead,
         * so must get the handler context in local thead set to memcached
         * session
         */
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        if (StringUtils.equalsIgnoreCase("XMLHttpRequest", request.getHeader("x-requested-with"))
                || StringUtils.equalsIgnoreCase("XMLHttpRequest", request.getHeader("X-Requested-With"))) {
            String jsonObject = "{\"success\":true}";
            String contentType = "application/json";
            response.setContentType(contentType);
            response.setCharacterEncoding(Constants.UTF_8);
            PrintWriter out = response.getWriter();
            out.print(jsonObject);
            out.flush();
            out.close();
            return;
        }


        super.onAuthenticationSuccess(request, response, authentication);
    }

}
