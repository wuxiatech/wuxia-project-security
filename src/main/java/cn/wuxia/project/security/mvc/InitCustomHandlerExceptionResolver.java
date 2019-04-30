/**
 *
 */
package cn.wuxia.project.security.mvc;

import cn.wuxia.project.security.core.exception.NeedLoginException;
import cn.wuxia.common.spring.mvc.resolver.CustomSpringMvcHandlerExceptionResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一异常处理器
 *
 * @author songlin.li
 * @since 2014-12-04
 */
public class InitCustomHandlerExceptionResolver extends CustomSpringMvcHandlerExceptionResolver implements Ordered{
    protected final  Logger logger = LoggerFactory.getLogger(InitCustomHandlerExceptionResolver.class);

    @Override
    public int getOrder() {
        /**
         * 优先级最高
         */
        return -2;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.warn(request.getRequestURI()+"->{}", ex.getMessage());
        logger.info("Request:{},Handler:{},Exception:{}",request.getRequestURI(),handler, ex.getClass().getName() );
        if (ex instanceof NeedLoginException) {
            response.setStatus(401);
            return new ModelAndView("redirect:/auth/login");
        } else {
            return super.resolveException(request, response, handler, ex);
        }
    }

}
