/*
 * Created on :2016年7月8日
 * Author     :songlin
 * Change History
 * Version       Date         Author           Reason
 * <Ver.No>     <date>        <who modify>       <reason>
 * Copyright 2014-2020 www.ibmall.cn All right reserved.
 */
package cn.wuxia.project.security.core.enums;

import cn.wuxia.common.util.StringUtil;

public enum SystemType {
    SYS_WEBSERVICE("ws", "接口系统"), SYS_BASE("admin", "超管系统"), SYS_API("api", "接口");

    String subdomain;

    String displayName;

    SystemType(String subdomain, String displayName) {
        this.subdomain = subdomain;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public static SystemType getBy(String subdomain) {
        for (SystemType type : SystemType.values()) {
            if (StringUtil.equalsIgnoreCase(subdomain, type.getSubdomain())) {
                return type;
            }
        }
        return null;
    }
}
