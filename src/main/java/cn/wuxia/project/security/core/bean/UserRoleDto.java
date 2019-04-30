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

import cn.wuxia.project.security.core.entity.SecurityRole;
import com.google.common.collect.Lists;

public class UserRoleDto implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -6292314024093297468L;

    private String userId;

    private List<SecurityRole> roles = Lists.newArrayList();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<SecurityRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SecurityRole> roles) {
        this.roles = roles;
    }

}
