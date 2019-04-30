/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.dao;

import java.util.List;

import cn.wuxia.project.security.core.entity.SecurityRole;
import cn.wuxia.project.basic.core.common.BaseCommonDao;
import cn.wuxia.project.security.core.entity.SecurityUserRoleRef;
import org.springframework.stereotype.Component;

@Component
public class SecurityUserRoleDao extends BaseCommonDao<SecurityUserRoleRef, String> {

    public List<SecurityRole> findByUserId(String userId) {
        String hql = "select r  from SecurityUserRoleRef ur, SecurityRole r where ur.roleId = r.id and ur.userId = ?";
        return find(hql, userId);
    }

    public void deleteByUserId(String userId) {
        String hql = "delete SecurityUserRoleRef where userId=?";
        super.batchExecute(hql, userId);
    }

    public void deleteByRoleId(String roleId) {
        String hql = "delete SecurityUserRoleRef where roleId=?";
        super.batchExecute(hql, roleId);
    }
}
