/*
* Created on :08 Nov, 2014
* Author     :huangzhihua
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.project.security.core.bean;

import java.io.Serializable;
import java.util.List;

import cn.wuxia.project.security.core.entity.SecurityPermission;
import com.google.common.collect.Lists;

public class RolePermissionsDto implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -188156146470152682L;

    private String roleId;

    private String roleName;

    private String roleDesc;

    private List<SecurityPermission> permissions = Lists.newArrayList();

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public List<SecurityPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SecurityPermission> permissions) {
        this.permissions = permissions;
    }

}
