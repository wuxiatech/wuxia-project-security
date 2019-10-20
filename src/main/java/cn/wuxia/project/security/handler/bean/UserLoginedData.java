/*
 * Created on :08 Nov, 2014
 * Author     :huangzhihua
 * Change History
 * Version       Date         Author           Reason
 * <Ver.No>     <date>        <who modify>       <reason>
 * Copyright 2014-2020 wuxia.tech All right reserved.
 */
package cn.wuxia.project.security.handler.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UserLoginedData implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private String id; // 用户id

    private String accountName;//账号

    private String openid; //详细信息id

    private String displayName;// 用户昵称

    private String logo;// 用户头像，可以为url

    private String city; //用户所在城市

    private String gender; //用户性别

    private String realName; //用户真实姓名

    private String permissionNames;

    private String roleName;


}
