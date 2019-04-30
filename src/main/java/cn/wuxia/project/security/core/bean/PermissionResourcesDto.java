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

import cn.wuxia.project.security.core.entity.SecurityResources;
import cn.wuxia.project.security.core.enums.SystemType;
import com.google.common.collect.Lists;

public class PermissionResourcesDto implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -188156146470152682L;

    private String permissionId;

    private String permissionName;

    private String permissionDesc;

    private SystemType systemType;

    private List<SecurityResources> resources = Lists.newArrayList();

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionDesc() {
        return permissionDesc;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    public SystemType getSystemType() {
        return systemType;
    }

    public void setSystemType(SystemType systemType) {
        this.systemType = systemType;
    }

    public List<SecurityResources> getResources() {
        return resources;
    }

    public void setResources(List<SecurityResources> resources) {
        this.resources = resources;
    }

}
