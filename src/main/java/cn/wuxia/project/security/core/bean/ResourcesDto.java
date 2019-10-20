/*
 * Created on :08 Nov, 2014
 * Author     :huangzhihua
 * Change History
 * Version       Date         Author           Reason
 * <Ver.No>     <date>        <who modify>       <reason>
 * Copyright 2014-2020 wuxia.tech All right reserved.
 */
package cn.wuxia.project.security.core.bean;

import cn.wuxia.common.util.StringUtil;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class ResourcesDto implements Serializable {


    private static final long serialVersionUID = 2214830151081795587L;
    String uri;

    String systemType;


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

}
