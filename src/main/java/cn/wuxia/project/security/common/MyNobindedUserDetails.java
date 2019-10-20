package cn.wuxia.project.security.common;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * 用户访问请求的上下文信息
 *
 * @author songlin.li
 */
@Getter
@Setter
public class MyNobindedUserDetails extends MyUserDetails implements Serializable {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1451533530770930083L;

    private String unionid;

    private String openid;

    private String appid;

    private String avatar;

    public MyNobindedUserDetails(String loginName, String userPassword, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                                 boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) throws IllegalArgumentException {
        super(loginName, userPassword, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }



    /***
     * Get detailed information of the currently logged on user must rewrite the
     * times method
     **/
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MyNobindedUserDetails) {
            MyNobindedUserDetails ud = (MyNobindedUserDetails) obj;
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


}
