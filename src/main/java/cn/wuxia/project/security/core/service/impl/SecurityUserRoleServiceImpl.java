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
import cn.wuxia.project.security.core.bean.UserRoleDto;
import cn.wuxia.project.security.core.dao.SecurityRoleDao;
import cn.wuxia.project.security.core.dao.SecurityUserRoleDao;
import cn.wuxia.project.security.core.entity.SecurityRole;
import cn.wuxia.project.security.core.entity.SecurityUserRoleRef;
import cn.wuxia.project.security.core.service.SecurityUserRoleService;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class SecurityUserRoleServiceImpl extends CommonServiceImpl<SecurityUserRoleRef, String> implements SecurityUserRoleService {
    @Autowired
    private SecurityUserRoleDao securityUserRoleDao;

    @Autowired
    private SecurityRoleDao securityRoleDao;

    @Override
    protected CommonDao<SecurityUserRoleRef, String> getCommonDao() {
        return securityUserRoleDao;
    }

    @Override
    public List<SecurityRole> findAllRole() {
        return securityRoleDao.findAll(new Sort("roleName"));
    }

    @Override
    public UserRoleDto getUserRole(String userId) {
        List<SecurityRole> roles = securityUserRoleDao.findByUserId(userId);
        UserRoleDto dto = new UserRoleDto();
        dto.setRoles(roles);
        return dto;
    }

    @Override
    public void saveUserRole(UserRoleDto dto) {
        if (StringUtil.isNotBlank(dto.getUserId())) {
            securityUserRoleDao.deleteByUserId(dto.getUserId());
        } else {
            throw new AppServiceException("userId 为空");
        }
        Set<SecurityUserRoleRef> sets = Sets.newHashSet();
        for (SecurityRole sr : dto.getRoles()) {
            if (StringUtil.isNotBlank(sr.getId())) {
                sets.add(new SecurityUserRoleRef(dto.getUserId(), sr.getId()));
            }
        }
        try {
            batchSave(sets);
        } catch (
                AppDaoException e) {
            throw new AppServiceException("保存失败");
        }
    }

}
