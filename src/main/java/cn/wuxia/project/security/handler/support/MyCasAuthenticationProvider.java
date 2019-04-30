package cn.wuxia.project.security.handler.support;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.client.util.URIBuilder;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class MyCasAuthenticationProvider extends CasAuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(MyCasAuthenticationProvider.class);

    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private ServiceProperties serviceProperties;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!this.supports(authentication.getClass())) {
            return null;
        } else if (authentication instanceof UsernamePasswordAuthenticationToken && !"_cas_stateful_".equals(authentication.getPrincipal().toString())
                && !"_cas_stateless_".equals(authentication.getPrincipal().toString())) {
            return null;
        } else if (authentication instanceof CasAuthenticationToken) {
            if (getKey().hashCode() == ((CasAuthenticationToken) authentication).getKeyHash()) {
                return authentication;
            } else {
                throw new BadCredentialsException(this.messages.getMessage("CasAuthenticationProvider.incorrectKey",
                        "The presented CasAuthenticationToken does not contain the expected key"));
            }
        } else if (authentication.getCredentials() != null && !"".equals(authentication.getCredentials())) {
            boolean stateless = false;
            if (authentication instanceof UsernamePasswordAuthenticationToken && "_cas_stateless_".equals(authentication.getPrincipal())) {
                stateless = true;
            }

            CasAuthenticationToken result = null;
            if (stateless) {
                result = getStatelessTicketCache().getByTicketId(authentication.getCredentials().toString());
            }

            if (result == null) {
                result = this.authenticateNow(authentication);
                result.setDetails(authentication.getDetails());
            }

            if (stateless) {
                getStatelessTicketCache().putTicketInCache(result);
            }

            return result;
        } else {
            throw new BadCredentialsException(
                    this.messages.getMessage("CasAuthenticationProvider.noServiceTicket", "Failed to provide a CAS service ticket to validate"));
        }
    }

    private CasAuthenticationToken authenticateNow(Authentication authentication) throws AuthenticationException {
        try {
            Assertion assertion = getTicketValidator().validate(authentication.getCredentials().toString(), this.getServiceUrl(authentication));
            UserDetails userDetails = this.loadUserByAssertion(assertion);
            userDetailsChecker.check(userDetails);
            return new CasAuthenticationToken(getKey(), userDetails, authentication.getCredentials(),
                    authoritiesMapper.mapAuthorities(userDetails.getAuthorities()), userDetails, assertion);
        } catch (TicketValidationException var4) {
            throw new BadCredentialsException(var4.getMessage(), var4);
        }
    }

    private String getServiceUrl(Authentication authentication) {
        String serviceUrl;
        if (authentication.getDetails() instanceof ServiceAuthenticationDetails) {
            serviceUrl = ((ServiceAuthenticationDetails) authentication.getDetails()).getServiceUrl();
        } else {
            if (this.serviceProperties == null) {
                throw new IllegalStateException(
                        "serviceProperties cannot be null unless Authentication.getDetails() implements ServiceAuthenticationDetails.");
            }

            if (this.serviceProperties.getService() == null) {
                throw new IllegalStateException(
                        "serviceProperties.getService() cannot be null unless Authentication.getDetails() implements ServiceAuthenticationDetails.");
            }

            serviceUrl = getServiceUrl(this.serviceProperties);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("serviceUrl = " + serviceUrl);
        }

        return serviceUrl;
    }

    private String getServiceUrl(ServiceProperties serviceProperties) {
        String service = serviceProperties.getService();
        logger.info("service:{}", service);
        URIBuilder builder = null;
        if (!service.startsWith("https://") && !service.startsWith("http://")) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            logger.info("serverName:{}", request.getServerName());
            builder = new URIBuilder(false);
            builder.setScheme(request.isSecure() ? "https" : "http");
            builder.setHost(request.getServerName());
            if(request.getServerPort() != 80)
                builder.setPort(request.getServerPort());
            builder.setPath(service);
        } else {
            builder = new URIBuilder(service, false);
        }
        /**
         * 重写补全
         */
        return builder.toString();
    }

    @Override
    public void setServiceProperties(ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }
}
