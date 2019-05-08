package cn.wuxia.project.security.handler.support;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.google.common.collect.Lists;

import cn.wuxia.project.security.core.bean.ResourcesPermissionsDto;
import cn.wuxia.project.security.core.enums.SystemType;
import cn.wuxia.project.security.core.service.SecurityPermissionResourcesService;

/**
 * 
 * [ticket id]
 * Description of the class 
 * @author songlin.li
 * @see MySecurityMetadataSource
 * @ Version : V<Ver.No> <2013年8月5日>
 */
public class MySecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private SecurityPermissionResourcesService securityResourcesService;

    /**
     * 系统类型
     */
    private String systemType;

    // return the roleList of requestionURLs
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        Collection<ConfigAttribute> atts = Lists.newArrayList();
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        // if uri matchs  need login uri
        for (ResourcesPermissionsDto dburi : securityResourcesService.findLoginResources(systemType)) {
            RequestMatcher pathMatcher = new AntPathRequestMatcher(dburi.getUri());
            if (pathMatcher.matches(request)) {
                for (String permissionName : dburi.getPermissions()) {
                    atts.add(new SecurityConfig(permissionName));
                }
                return atts;
            }
        }
        return atts;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return new ArrayList<ConfigAttribute>();
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

}
