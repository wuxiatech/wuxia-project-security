package cn.wuxia.project.security.handler.support;

import java.io.IOException;
import java.util.Date;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

import cn.wuxia.project.security.common.MySecurityHttpServeltRequestWrapper;
import cn.wuxia.common.spring.SpringContextHolder;
import cn.wuxia.common.util.StringUtil;

public class MySecurityFilter extends AbstractSecurityInterceptor implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(MySecurityFilter.class);

    // applicationContext-handler.xml's myFilter property
    // securityMetadataSource，
    // other component is defined by AbstractSecurityInterceptor
    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    /**
     * object is FilterInvocation object super.beforeInvocation(fi);source code
     * 1.The limits of authority for request resources run
     * Collection<ConfigAttribute> attributes =
     * SecurityMetadataSource.getAttributes(object); 2.Have authority
     * this.accessDecisionManager.decide(authenticated, object, attributes);
     **/
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        HttpServletRequest srequest = new MySecurityHttpServeltRequestWrapper((HttpServletRequest) request);
        String ip = srequest.getRemoteAddr();
        String uri = srequest.getRequestURI();
        /**
         * session from memcached, spring handler save user info in local
         * thead, so must put the handler context in local thead
         */
        HttpSession session = srequest.getSession();
        SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (context != null)
            SecurityContextHolder.setContext(context);

        logger.debug("$$$[" + ip + "] Begin handler request. URI:" + uri);

        FilterInvocation fi = new FilterInvocation(srequest, response, chain);
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;
        if (costTime >= 2000 && costTime <= 5 * 1000) {
            logger.info("$$$[" + ip + "] End handler request. Current request cost time(Level-1): " + costTime + " ms. URI:" + uri);
        } else if (costTime > 5 * 1000 && costTime <= 10 * 1000) {
            logger.info("$$$[" + ip + "] End handler request. Current request cost time(Level-2): " + (costTime) + " ms. URI:" + uri);
        } else if (costTime > 10 * 1000) {
            logger.warn("$$$[" + ip + "] End handler request. Current request cost time(Level-3): " + (costTime) + " ms. URI:" + uri);
            if (StringUtil.indexOf(uri, "/api/") == -1 && StringUtil.indexOf(uri, "/auth/") == -1 && !StringUtil.equalsIgnoreCase(ip, "127.0.0.1")
                    && !StringUtil.equalsIgnoreCase(ip, "0:0:0:0:0:0:0:1")) {
                String httpPath = srequest.getScheme() + "://" + srequest.getHeader("server-name");
                if (!StringUtil.equals(srequest.getHeader("server-port"), "80")) {
                    httpPath = httpPath + ":" + srequest.getHeader("server-port");
                }
                String remark = httpPath + uri;
                if (StringUtil.isNotBlank(srequest.getQueryString())) {
                    remark += "?" + srequest.getQueryString();
                }
                saveSystemStatistics(uri, costTime, ip, remark);
            }
        }
    }

    private void saveSystemStatistics(final String uri, final long costTime, final String ip, final String remark) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JdbcTemplate jdbcTemplate = null;
                try {
                    jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
                } catch (Exception e) {
                    jdbcTemplate = new JdbcTemplate(SpringContextHolder.getBean("baseDataSource"));
                } finally {
                    String sql = "INSERT INTO sys_cost_time_statistics ( NAME, cost_time, ip, access_datetime, type, remark ) VALUES (?, ?, ?, ?, ?, ?);";
                    try {
                        jdbcTemplate.update(sql, new Object[]{uri, costTime, ip, new Date(), 2, remark});
                    }catch (Exception e1){
                        logger.warn("", e1.getMessage());
                    }
                }

            }
        });
        thread.start();
    }

    public FilterInvocationSecurityMetadataSource getSecurityMetadataSource() {
        return securityMetadataSource;
    }

    public void setSecurityMetadataSource(FilterInvocationSecurityMetadataSource securityMetadataSource) {
        this.securityMetadataSource = securityMetadataSource;
    }

    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
    }

    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public Class<? extends Object> getSecureObjectClass() {
        // MyAccessDecisionManager's supports aspects must be put back to true,
        // otherwise, it will remind type error
        return FilterInvocation.class;
    }

}
