/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.service.impl;

import cn.wuxia.common.exception.AppDaoException;
import cn.wuxia.common.exception.AppServiceException;
import cn.wuxia.common.orm.query.Sort;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.project.common.dao.CommonDao;
import cn.wuxia.project.common.service.impl.CommonServiceImpl;
import cn.wuxia.project.common.support.CacheConstants;
import cn.wuxia.project.security.core.bean.PermissionResourcesDto;
import cn.wuxia.project.security.core.bean.ResourcesPermissionsDto;
import cn.wuxia.project.security.core.dao.SecurityPermissionDao;
import cn.wuxia.project.security.core.dao.SecurityPermissionResourcesDao;
import cn.wuxia.project.security.core.dao.SecurityResourcesDao;
import cn.wuxia.project.security.core.dao.SecurityRolePermissionDao;
import cn.wuxia.project.security.core.entity.SecurityPermission;
import cn.wuxia.project.security.core.entity.SecurityPermissionResourcesRef;
import cn.wuxia.project.security.core.entity.SecurityResources;
import cn.wuxia.project.security.core.service.SecurityPermissionResourcesService;
import com.google.common.collect.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class SecurityPermissionResourcesServiceImpl extends CommonServiceImpl<SecurityPermissionResourcesRef, String>
        implements SecurityPermissionResourcesService {
    @Autowired
    private SecurityPermissionResourcesDao securityPermissionResoucesDao;

    @Autowired
    private SecurityResourcesDao securityResoucesDao;

    @Autowired
    private SecurityPermissionDao securityPermissionDao;

    @Autowired
    private SecurityRolePermissionDao securityRolePermissionDao;

    @Override
    protected CommonDao<SecurityPermissionResourcesRef, String> getCommonDao() {
        return securityPermissionResoucesDao;
    }

    @Cacheable(key = CacheConstants.CACHED_KEY_DEFAULT + "+#systemType", value = CacheConstants.CACHED_VALUE_1_HOUR)
    @Override
    public List<ResourcesPermissionsDto> findLoginResources(String systemType) {
        return securityPermissionResoucesDao.findLoginResourcesByType(systemType);
    }

    @Override
    @CacheEvict(key = "#root.targetClass +'.findLoginResources'+#systemType", value = CacheConstants.CACHED_VALUE_1_HOUR)
    public void cleanLoginResourcesCache(String systemType) {

    }

    @Override
    public List<SecurityResources> findResourcesByPermissionName(String permissionName) {
        return securityPermissionResoucesDao.findByPermissionName(permissionName);
    }

    @Override
    public void saveResouces(SecurityResources resources) {
        securityResoucesDao.save(resources);
    }

    @Override
    public SecurityResources getResoucesById(String resourcesId) {
        return securityResoucesDao.get(resourcesId);
    }

    @Override
    public void deleteResoucesById(String resourcesId) {
        securityPermissionResoucesDao.deleteByResourcesId(resourcesId);
        securityResoucesDao.delete(resourcesId);
    }

    @Override
    public List<SecurityResources> findAllResources() {
        return securityResoucesDao.findAll(new Sort("type", "uri"));
    }

    @Cacheable(value = CacheConstants.CACHED_VALUE_1_HOUR, key = CacheConstants.CACHED_KEY_DEFAULT+"+#systemType")
    @Override
    public List<SecurityResources> findResourcesByType(String systemType) {
        if (systemType == null) {
            return securityResoucesDao.findAll(new Sort("uri"));
        }
        return securityResoucesDao.findBy("type", systemType, "uri", true);
    }


    @Override
    public PermissionResourcesDto getPermissionResource(String permissionId) {
        PermissionResourcesDto dto = new PermissionResourcesDto();
        if (StringUtil.isBlank(permissionId)) {
            return dto;
        }
        List<SecurityResources> resources = securityPermissionResoucesDao.findByPermissionId(permissionId);
        SecurityPermission sr = securityPermissionDao.get(permissionId);
        dto.setResources(resources);
        BeanUtils.copyProperties(sr, dto);
        dto.setPermissionId(permissionId);
        return dto;
    }

    @Override
    public void savePermissionResources(PermissionResourcesDto dto) {
        if (StringUtil.isNotBlank(dto.getPermissionId())) {
            securityPermissionResoucesDao.deleteByPermissionId(dto.getPermissionId());
            SecurityPermission spermission = securityPermissionDao.findById(dto.getPermissionId());
            if (!StringUtil.equals(spermission.getPermissionName(), dto.getPermissionName())
                    || !StringUtil.equals(spermission.getPermissionDesc(), dto.getPermissionDesc())) {
                spermission.setPermissionName(dto.getPermissionName());
                spermission.setPermissionDesc(dto.getPermissionDesc());
                spermission.setSystemType(dto.getSystemType());
                securityPermissionDao.save(spermission);
            }
        } else {
            SecurityPermission spermission = new SecurityPermission(dto.getPermissionName(), dto.getPermissionDesc(), dto.getSystemType());
            securityPermissionDao.save(spermission);
            dto.setPermissionId(spermission.getId());
        }
        Set<SecurityPermissionResourcesRef> refs = Sets.newHashSet();
        for (SecurityResources resources : dto.getResources()) {
            /**
             * 相同的系统资源方允许保存
             */
            if (StringUtil.isNotBlank(resources.getId())) {
                refs.add(new SecurityPermissionResourcesRef(dto.getPermissionId(), resources.getId()));
            }
        }
        try {
            super.batchSave(refs);
        } catch (
                AppDaoException e) {
            throw new AppServiceException("保存失败");
        }
    }

    @Override
    public List<SecurityPermission> findAllPermissions() {
        return securityPermissionDao.findAll(new Sort("systemType", "permissionName"));
    }

    @Override
    public List<SecurityPermission> findPermissionsByType(String systemType) {
        if (systemType == null) {
            return securityPermissionDao.findAll(new Sort("permissionName"));
        } else {
            return securityPermissionDao.findBy("systemType", systemType, "permissionName", true);
        }
    }

    @Override
    public void deletePermission(String permissionId) {
        /**
         * 删除权限资源关系
         */
        securityPermissionResoucesDao.deleteByPermissionId(permissionId);
        /**
         * 删除角色权限关系
         */
        securityRolePermissionDao.deleteByPermissionId(permissionId);
        /**
         * 物理删除权限
         */
        securityPermissionDao.delete(permissionId);

    }
}
