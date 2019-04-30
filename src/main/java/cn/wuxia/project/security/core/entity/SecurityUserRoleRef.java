package cn.wuxia.project.security.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import cn.wuxia.project.common.model.ModifyInfoEntity;

@Entity
@Table(name = "security_user_role_ref")
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SecurityUserRoleRef extends ModifyInfoEntity {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 57508246925051693L;

    private String userId;

    private String roleId;

    public SecurityUserRoleRef() {
        super();
    }

    public SecurityUserRoleRef(String userId, String roleId) {
        super();
        this.userId = userId;
        this.roleId = roleId;
    }

    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "role_id")
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

}
