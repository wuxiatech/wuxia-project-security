/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.service.impl;

import cn.wuxia.project.security.core.bean.ResourcesDto;
import cn.wuxia.project.security.core.bean.RolePermissionsDto;
import cn.wuxia.project.security.core.dao.SecurityRoleDao;
import cn.wuxia.project.security.core.dao.SecurityRolePermissionDao;
import cn.wuxia.project.security.core.dao.SecurityUserRoleDao;
import cn.wuxia.project.security.core.entity.SecurityPermission;
import cn.wuxia.project.security.core.entity.SecurityRole;
import cn.wuxia.project.security.core.entity.SecurityRolePermissionRef;
import cn.wuxia.project.security.core.service.SecurityRolePermissionsService;
import cn.wuxia.project.common.dao.CommonDao;
import cn.wuxia.project.common.service.impl.CommonServiceImpl;
import cn.wuxia.common.util.StringUtil;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class SecurityRolePermissionServiceImpl extends CommonServiceImpl<SecurityRolePermissionRef, String>
        implements SecurityRolePermissionsService {
    @Autowired
    private SecurityRolePermissionDao securityRolePermissionDao;

    @Autowired
    private SecurityRoleDao securityRoleDao;

    @Autowired
    private SecurityUserRoleDao securityUserRoleDao;

    @Override
    protected CommonDao<SecurityRolePermissionRef, String> getCommonDao() {
        return securityRolePermissionDao;
    }


    @Override
    public List<ResourcesDto> findResourcesByRoleName(String roleName) {
        return securityRoleDao.findResourcesByRoleName(roleName);
    }

    @Override
    public RolePermissionsDto getRolePermission(String roleId) {
        RolePermissionsDto dto = new RolePermissionsDto();
        if (StringUtil.isBlank(roleId))
            return dto;

        List<SecurityPermission> permissions = securityRolePermissionDao.findByRoleId(roleId);
        SecurityRole sr = securityRoleDao.get(roleId);
        dto.setPermissions(permissions);
        dto.setRoleId(sr.getId());
        dto.setRoleName(sr.getRoleName());
        dto.setRoleDesc(sr.getRoleDesc());
        return dto;
    }

    @Override

    public void saveRolePermissions(RolePermissionsDto dto) {
        if (StringUtil.isNotBlank(dto.getRoleId())) {
            securityRolePermissionDao.deleteByRoleId(dto.getRoleId());
            SecurityRole srole = securityRoleDao.getEntityById(dto.getRoleId());
            if (!StringUtil.equals(srole.getRoleName(), dto.getRoleName()) || !StringUtil.equals(srole.getRoleDesc(), dto.getRoleDesc())) {
                srole.setRoleName(dto.getRoleName());
                srole.setRoleDesc(dto.getRoleDesc());
                securityRoleDao.save(srole);
            }
        } else {
            SecurityRole srole = new SecurityRole(dto.getRoleName(), dto.getRoleDesc());
            securityRoleDao.save(srole);
            dto.setRoleId(srole.getId());
        }
        Set<SecurityRolePermissionRef> refs = Sets.newHashSet();
        for (SecurityPermission permission : dto.getPermissions()) {
            if (StringUtil.isNotBlank(permission.getId())) {
                refs.add(new SecurityRolePermissionRef(dto.getRoleId(), permission.getId()));
            }
        }
        super.batchSave(refs);
    }

    @Override
    public void deleteRole(String roleId) {
        /**
         * 物理删除角色权限关系
         */
        securityRolePermissionDao.deleteByRoleId(roleId);
        /**
         * 物理删除用户角色关系
         */
        securityUserRoleDao.deleteByRoleId(roleId);
        /**
         * 物理删除角色
         */
        securityRoleDao.delete(roleId);
    }
}
