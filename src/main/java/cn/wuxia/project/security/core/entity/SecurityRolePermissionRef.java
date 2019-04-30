package cn.wuxia.project.security.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import cn.wuxia.project.common.model.ModifyInfoEntity;

@Entity
@Table(name =  "security_role_permission_ref")
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SecurityRolePermissionRef extends ModifyInfoEntity {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -1573501084357585811L;

    private String permissionId;

    private String roleId;

    public SecurityRolePermissionRef() {
        super();
    }

    public SecurityRolePermissionRef(String roleId, String permissionId) {
        super();
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    @Column(name = "permission_id")
    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    @Column(name = "role_id")
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

}
