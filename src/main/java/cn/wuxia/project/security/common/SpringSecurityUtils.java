/**
 * Copyright (c) 2005-2009 springside.org.cn Licensed under the Apache License,
 * Version 2.0 (the "License"); $Id: SpringSecurityUtils.java 1185 2010-08-29
 * 15:56:19Z calvinxiu $
 */
package cn.wuxia.project.security.common;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * SpringSecurity Tool. support SpringSecurity 3.0.x.
 * 
 * @author calvin
 */
public class SpringSecurityUtils {
    /**
     * get current user, return SpringSecurity User class or child class, if not
     * login then return null.
     */
    @SuppressWarnings("unchecked")
    public static <T extends User> T getCurrentUser() {
        ServletRequestAttributes requestAttr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttr != null) {
            HttpServletRequest request = requestAttr.getRequest();
            SecurityContext springSecurityContext = request.getSession().getAttribute("SPRING_SECURITY_CONTEXT") == null ? null
                    : (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
            if (springSecurityContext != null && springSecurityContext.getAuthentication() != null) {
                T userDetails = (T)springSecurityContext.getAuthentication().getPrincipal();
                if (userDetails != null && userDetails instanceof User) {
                    return  userDetails;
                }
            }
        }
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return null;
        }

        return (T) principal;
    }

    /**
     * get current user name, if not login then return empty string.
     */
    public static String getCurrentUserName() {
        Authentication authentication = getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "";
        }

        return authentication.getName();
    }

    /**
     * get user IP, if not login then return empty string.
     */
    public static String getCurrentUserIp() {
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return "";
        }

        Object details = authentication.getDetails();
        if (!(details instanceof WebAuthenticationDetails)) {
            return "";
        }

        WebAuthenticationDetails webDetails = (WebAuthenticationDetails) details;
        return webDetails.getRemoteAddress();
    }

    /**
     * if user has role then return true else return false.
     */
    public static boolean hasAnyRole(String... roles) {
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            return false;
        }

        Collection<GrantedAuthority> grantedAuthorityList = (Collection<GrantedAuthority>) authentication.getAuthorities();
        for (String role : roles) {
            for (GrantedAuthority authority : grantedAuthorityList) {
                if (role.equals(authority.getAuthority())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * cached User Details in Security Context.
     * 
     * @param userDetails init user detail.
     * @param request user info, ip and others.
     */
    public static void saveUserDetailsToContext(UserDetails userDetails, HttpServletRequest request) {
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());

        if (request != null) {
            authentication.setDetails(new WebAuthenticationDetails(request));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * get Authentication.
     */
    public static Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null) {
            return null;
        }
        return context.getAuthentication();
    }
}
