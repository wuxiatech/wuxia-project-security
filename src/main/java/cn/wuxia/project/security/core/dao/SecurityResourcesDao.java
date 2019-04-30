/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.dao;

import java.util.Map;

import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.project.security.core.entity.SecurityResources;
import org.springframework.stereotype.Component;

import cn.wuxia.common.spring.SpringContextHolder;

@Component
public class SecurityResourcesDao extends BaseCommonDao<SecurityResources, Long> {
    private Map<String, String> queryMap = SpringContextHolder.getBean("resources-query");

}
