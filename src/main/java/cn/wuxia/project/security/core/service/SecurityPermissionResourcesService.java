/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.service;

import java.util.List;

import cn.wuxia.project.security.core.bean.PermissionResourcesDto;
import cn.wuxia.project.security.core.bean.ResourcesPermissionsDto;
import cn.wuxia.project.security.core.entity.SecurityPermission;
import cn.wuxia.project.security.core.entity.SecurityPermissionResourcesRef;
import cn.wuxia.project.security.core.entity.SecurityResources;
import cn.wuxia.project.security.core.enums.SystemType;
import cn.wuxia.project.common.service.CommonService;

public interface SecurityPermissionResourcesService extends CommonService<SecurityPermissionResourcesRef, String> {


    /**
     * 资源权限
     *
     * @param systemType
     * @return
     * @author songlin
     */
    public List<ResourcesPermissionsDto> findLoginResources(SystemType systemType);

    /**
     * 清空findLoginResources的缓存
     *
     * @param systemType
     * @return
     * @author songlin
     */
    public void cleanLoginResourcesCache(SystemType systemType);

    /**
     * 资源权限
     *
     * @param permissionName
     * @return
     * @author songlin
     */
    public List<SecurityResources> findResourcesByPermissionName(String permissionName);

    /**
     * 保存资源并更新资源权限
     *
     * @param resources
     * @author songlin
     */
    public void saveResouces(SecurityResources resources);

    /**
     * 获取修改
     *
     * @param resourcesId
     * @return
     * @author songlin
     */
    public SecurityResources getResoucesById(String resourcesId);

    /**
     * 删除资源
     *
     * @param resourcesId
     * @author songlin
     */
    public void deleteResoucesById(String resourcesId);

    /**
     * 根据type查找
     *
     * @return
     * @author songlin
     */
    public List<SecurityResources> findAllResources();

    /**
     * 根据type查找
     *
     * @param systemType
     * @return
     * @author songlin
     */
    public List<SecurityResources> findResourcesByType(SystemType systemType);


    /**
     * 某个权限的资源
     *
     * @param permissionId
     * @return
     * @author songlin
     */
    public PermissionResourcesDto getPermissionResource(String permissionId);

    /**
     * 保存权限与资源
     *
     * @param dto
     * @author songlin
     */
    public void savePermissionResources(PermissionResourcesDto dto);

    /**
     * 查找所有权限
     *
     * @return
     * @author songlin
     */
    public List<SecurityPermission> findAllPermissions();

    /**
     * 查找所有权限
     *
     * @param systemType
     * @return
     * @author songlin
     */
    public List<SecurityPermission> findPermissionsByType(SystemType systemType);

    /**
     * 删除权限
     *
     * @author songlin
     */
    public void deletePermission(String permissionId);
}
