/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.dao;

import java.util.List;

import cn.wuxia.project.security.core.entity.SecurityPermission;
import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.project.security.core.entity.SecurityRolePermissionRef;
import org.springframework.stereotype.Component;

@Component
public class SecurityRolePermissionDao extends BaseCommonDao<SecurityRolePermissionRef, String> {

    public List<SecurityPermission> findByRoleId(String roleId) {
        String hql = "select p  from SecurityRolePermissionRef up, SecurityPermission p where up.permissionId = p.id and up.roleId = ?";
        return find(hql, roleId);
    }

    public void deleteByRoleId(String roleId) {
        String hql = "delete SecurityRolePermissionRef where roleId=?";
        super.batchExecute(hql, roleId);
    }

    public void deleteByPermissionId(String permissionId) {
        String hql = "delete SecurityRolePermissionRef where permissionId=?";
        super.batchExecute(hql, permissionId);
    }
}
