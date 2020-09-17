package cn.wuxia.project.security.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 用户访问请求的上下文信息
 *
 * @author songlin.li
 */
@JsonAutoDetect
@JsonIgnoreProperties(value = {"uid", "password", "authorities", "salt", "accountNonExpired", "accountNonLocked", "credentialsNonExpired",
        "enabled"})
@Getter
@Setter
public class MyUserDetails extends org.springframework.security.core.userdetails.User implements Serializable {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1451533530770930083L;

    private String displayName; // 当前登录用户名字

    private String uid; // 当前登录用户id

    private String mobile;// 绑定的手机号码

    private String clientIp; //用户访问的IP


    private Set<String> roles;

    public MyUserDetails(String loginName, String userPassword, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                         boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) throws IllegalArgumentException {
        super(loginName, userPassword, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public MyUserDetails(String loginName, String userPassword, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                         boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Collection<String> roles) throws IllegalArgumentException {
        super(loginName, userPassword, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.roles = Collections.unmodifiableSet(Sets.newConcurrentHashSet(roles));
    }

    /***
     * Get detailed information of the currently logged on user must rewrite the
     * times method
     **/
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MyUserDetails) {
            MyUserDetails ud = (MyUserDetails) obj;
            if (ud.getUsername().equals(this.getUsername())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void setRoles(Set<String> roles) {
        this.roles = Collections.unmodifiableSet(roles);
    }
}
