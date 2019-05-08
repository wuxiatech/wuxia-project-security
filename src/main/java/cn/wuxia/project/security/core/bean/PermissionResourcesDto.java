/*
 * Created on :08 Nov, 2014
 * Author     :huangzhihua
 * Change History
 * Version       Date         Author           Reason
 * <Ver.No>     <date>        <who modify>       <reason>
 * Copyright 2014-2020 www.ibmall.cn All right reserved.
 */
package cn.wuxia.project.security.core.bean;

import cn.wuxia.project.security.core.entity.SecurityResources;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class PermissionResourcesDto implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -188156146470152682L;

    private String permissionId;

    private String permissionName;

    private String permissionDesc;

    private String systemType;

    private List<SecurityResources> resources = Lists.newArrayList();


}
