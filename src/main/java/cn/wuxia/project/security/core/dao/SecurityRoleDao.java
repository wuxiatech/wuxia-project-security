/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.dao;

import cn.wuxia.project.security.core.bean.ResourcesDto;
import cn.wuxia.project.security.core.entity.SecurityRole;
import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.common.spring.SpringContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SecurityRoleDao extends BaseCommonDao<SecurityRole, String> {
    private Map<String, String> queryMap = SpringContextHolder.getBean("resources-query");



    /**
     * 资源权限
     *
     * @return
     * @author songlin
     */
    public List<ResourcesDto> findResourcesByRoleName(String roleName) {
        String sql = queryMap.get("findResourcesByRoleName_sql");
        return  query(sql, ResourcesDto.class, roleName);
    }
}
