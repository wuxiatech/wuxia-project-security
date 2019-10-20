/*
 * Created on :2016年7月8日
 * Author     :songlin
 * Change History
 * Version       Date         Author           Reason
 * <Ver.No>     <date>        <who modify>       <reason>
 * Copyright 2014-2020 wuxia.tech All right reserved.
 */
package cn.wuxia.project.security.core.enums;

import cn.wuxia.common.util.ListUtil;
import cn.wuxia.project.basic.core.conf.support.DicBean;
import cn.wuxia.project.basic.support.Functions;
import cn.wuxia.project.common.bean.SimpleFieldProperty;
import com.google.common.collect.Lists;

import java.util.List;

public class SystemType {
    private final static String SYSTEM_TYPE = "SYSTEM_TYPE";

    public static List<SimpleFieldProperty> values() {
        List<DicBean> dicBeans = Functions.getDicsByParentCode(SYSTEM_TYPE);
        List<SimpleFieldProperty> systemTypes = Lists.newArrayList();
        if (ListUtil.isNotEmpty(dicBeans)) {
            /**
             * 字典code对应value
             * 字典value对应name
             */
            dicBeans.forEach(dicBean -> systemTypes.add(new SimpleFieldProperty(dicBean.getValue(), dicBean.getCode())));
        }
        return systemTypes;
    }
}
