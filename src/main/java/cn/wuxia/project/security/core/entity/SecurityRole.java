package cn.wuxia.project.security.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import cn.wuxia.project.common.model.ModifyInfoEntity;

@Entity
@Table(name = "security_role")
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SecurityRole extends ModifyInfoEntity {

    private static final long serialVersionUID = 1227390629186486032L;

    private String roleName;

    private String roleDesc;

    public SecurityRole() {
        super();
    }

    public SecurityRole(String roleName, String roleDesc) {
        super();
        this.roleName = roleName;
        this.roleDesc = roleDesc;
    }

    @Column(name = "role_name")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Column(name = "role_desc")
    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

}
