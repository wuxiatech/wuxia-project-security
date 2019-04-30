/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.service;

import java.util.List;

import cn.wuxia.project.security.core.bean.ResourcesDto;
import cn.wuxia.project.security.core.bean.RolePermissionsDto;
import cn.wuxia.project.security.core.entity.SecurityRolePermissionRef;
import cn.wuxia.project.common.service.CommonService;

public interface SecurityRolePermissionsService extends CommonService<SecurityRolePermissionRef, String> {

    /**
     * 资源权限
     * @author songlin
     * @param roleName
     * @return
     */
    public List<ResourcesDto> findResourcesByRoleName(String roleName);
    /**
     * 某个角色的权限
     *
     * @param roleId
     * @return
     * @author songlin
     */
    public RolePermissionsDto getRolePermission(String roleId);

    /**
     * 保存角色与权限
     *
     * @param dto
     * @author songlin
     */
    public void saveRolePermissions(RolePermissionsDto dto);

    /**
     * 删除角色
     *
     * @param roleId
     * @author songlin
     */
    public void deleteRole(String roleId);

}
