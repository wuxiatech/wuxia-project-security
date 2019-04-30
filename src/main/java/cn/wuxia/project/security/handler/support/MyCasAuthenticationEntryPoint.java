package cn.wuxia.project.security.handler.support;

import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyCasAuthenticationEntryPoint extends CasAuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(MyCasAuthenticationEntryPoint.class);

    @Override
    protected String createServiceUrl(HttpServletRequest request, HttpServletResponse response) {
        String service = getServiceProperties().getService();
        URIBuilder builder = null;
        if (!service.startsWith("https://") && !service.startsWith("http://")) {
            builder = new URIBuilder(false);
            builder.setScheme(request.isSecure() ? "https" : "http");
            logger.info(request.getServerName());
            builder.setHost(request.getServerName());
            if(request.getServerPort() != 80 && request.getServerPort() != 443)
                builder.setPort(request.getServerPort());
            builder.setPath(service);
        } else {
            builder = new URIBuilder(service, super.getEncodeServiceUrlWithSessionId());
        }
        logger.info("url:{}", builder.toString());
        /**
         * 重写补全
         */
        return CommonUtils.constructServiceUrl((HttpServletRequest) null, response, builder.toString(), (String)null, getServiceProperties().getArtifactParameter(), false);
    }
}
