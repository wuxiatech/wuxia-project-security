/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.service;

import java.util.List;

import cn.wuxia.project.security.core.bean.UserRoleDto;
import cn.wuxia.project.security.core.entity.SecurityRole;
import cn.wuxia.project.security.core.entity.SecurityUserRoleRef;
import cn.wuxia.project.common.service.CommonService;

public interface SecurityUserRoleService extends CommonService<SecurityUserRoleRef, String> {

    /**
     * 查找所有角色
     * @author songlin
     * @return
     */
    public List<SecurityRole> findAllRole();

    /**
     * 某个用户的角色
     * @author songlin
     * @param userId
     * @return
     */
    public UserRoleDto getUserRole(String userId);

    public void saveUserRole(UserRoleDto dto);
}
