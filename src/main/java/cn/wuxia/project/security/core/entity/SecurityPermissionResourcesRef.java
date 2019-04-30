package cn.wuxia.project.security.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import cn.wuxia.project.common.model.ModifyInfoEntity;

@Entity
@Table(name = "security_permission_resources_ref")
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SecurityPermissionResourcesRef extends ModifyInfoEntity {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -1573501084357585811L;

    private String resourcesId;

    private String permissionId;

    public SecurityPermissionResourcesRef() {
        super();
    }

    public SecurityPermissionResourcesRef(String permissionId, String resourcesId) {
        super();
        this.permissionId = permissionId;
        this.resourcesId = resourcesId;
    }

    @Column(name = "resources_id")
    public String getResourcesId() {
        return resourcesId;
    }

    public void setResourcesId(String resourcesId) {
        this.resourcesId = resourcesId;
    }

    @Column(name = "permission_id")
    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

}
