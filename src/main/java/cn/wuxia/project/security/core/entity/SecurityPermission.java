package cn.wuxia.project.security.core.entity;

import javax.persistence.*;

import cn.wuxia.project.security.core.enums.SystemType;
import cn.wuxia.project.common.model.ModifyInfoEntity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "security_permission")
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SecurityPermission extends ModifyInfoEntity {

    private static final long serialVersionUID = 1227390629186486032L;

    private String permissionName;

    private String permissionDesc;

    private SystemType systemType;

    public SecurityPermission() {
        super();
    }

    public SecurityPermission(String permissionName, String permissionDesc, SystemType systemType) {
        super();
        this.permissionName = permissionName;
        this.permissionDesc = permissionDesc;
        this.systemType = systemType;
    }

    @Column(name = "permission_name")
    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Column(name = "permission_desc")
    public String getPermissionDesc() {
        return permissionDesc;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }


    @Enumerated(EnumType.STRING)
    @Column(name = "system_type")
    public SystemType getSystemType() {
        return systemType;
    }

    public void setSystemType(SystemType systemType) {
        this.systemType = systemType;
    }
}
