/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.dao;

import java.util.List;
import java.util.Map;

import cn.wuxia.project.security.core.bean.ResourcesPermissionsDto;
import cn.wuxia.project.security.core.entity.SecurityResources;
import cn.wuxia.project.security.core.enums.SystemType;
import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.project.security.core.entity.SecurityPermissionResourcesRef;
import org.springframework.stereotype.Component;

import cn.wuxia.common.spring.SpringContextHolder;

@Component
public class SecurityPermissionResourcesDao extends BaseCommonDao<SecurityPermissionResourcesRef, String> {
    private Map<String, String> queryMap = SpringContextHolder.getBean("resources-query");

    /**
     * 资源权限
     *
     * @return
     * @author songlin
     */
    public List<ResourcesPermissionsDto> findLoginResourcesByType(SystemType type) {
        String sql = queryMap.get("findResourcesAndPermissions_sql");
        return  query(sql, ResourcesPermissionsDto.class, type.name());
    }

    public List<SecurityResources> findByPermissionId(String permissionId) {
        String hql = "select r  from SecurityResources r, SecurityPermissionResourcesRef pr where pr.resourcesId = r.id and pr.permissionId = ?";
        return find(hql, permissionId);
    }

    public List<SecurityResources> findByPermissionName(String permissionName) {
        String hql = "select r  from SecurityResources r, SecurityPermissionResourcesRef pr, SecurityPermission p  where pr.resourcesId = r.id and pr.permissionId = p.id and p.permissionName=?";
        return find(hql, permissionName);
    }

    public void deleteByPermissionId(String permissionId) {
        String hql = "delete SecurityPermissionResourcesRef where permissionId=?";
        super.batchExecute(hql, permissionId);
    }

    public void deleteByResourcesId(String resourcesId) {
        String hql = "delete SecurityPermissionResourcesRef where resourcesId=?";
        super.batchExecute(hql, resourcesId);
    }
}
