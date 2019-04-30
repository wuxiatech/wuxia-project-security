/*
 * Created on :08 Nov, 2014
 * Author     :huangzhihua
 * Change History
 * Version       Date         Author           Reason
 * <Ver.No>     <date>        <who modify>       <reason>
 * Copyright 2014-2020 www.ibmall.cn All right reserved.
 */
package cn.wuxia.project.security.core.bean;

import cn.wuxia.common.util.StringUtil;
import com.google.common.collect.Lists;

import java.util.List;

public class ResourcesPermissionsDto extends ResourcesDto {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 2649488213222540519L;

    String permissions;


    public List<String> getPermissions() {
        if (StringUtil.isNotBlank(permissions))
            return Lists.newArrayList(StringUtil.split(permissions, ","));
        else
            return Lists.newArrayList();
    }


    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
