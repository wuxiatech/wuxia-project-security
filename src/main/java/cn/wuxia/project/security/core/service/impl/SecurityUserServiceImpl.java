/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.service.impl;

import cn.wuxia.common.exception.AppServiceException;
import cn.wuxia.project.security.core.dao.SecurityUserDao;
import cn.wuxia.project.security.core.entity.SecurityUser;
import cn.wuxia.project.security.core.service.SecurityUserService;
import cn.wuxia.project.common.dao.CommonDao;
import cn.wuxia.project.security.common.MyPasswordEncoder;
import cn.wuxia.project.common.service.impl.CommonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SecurityUserServiceImpl extends CommonServiceImpl<SecurityUser, String> implements SecurityUserService {
    @Autowired
    private SecurityUserDao securityUserDao;

    @Override
    protected CommonDao<SecurityUser, String> getCommonDao() {
        return securityUserDao;
    }

    @Override
    public void updatePasswd(String userid, String password) {
        SecurityUser cu = securityUserDao.findById(userid);
        MyPasswordEncoder encoder = new MyPasswordEncoder();
        String md5password = encoder.encodeMD5Password(password);
        cu.setPassword(MyPasswordEncoder.extractPassword(md5password));
        cu.setSalt(MyPasswordEncoder.extractSalt(md5password));
        save(cu);
    }

    /**
     * 修改密码
     *
     * @param userid
     * @param password
     * @param oldpassword
     * @author songlin
     */
    @Override
    public void updatePasswd(String userid, String password, String oldpassword) {
        SecurityUser cu = securityUserDao.findById(userid);
        MyPasswordEncoder encoder = new MyPasswordEncoder();
        /**
         * 借用description字段
         */
        if (!encoder.isPasswordValid(cu.getPassword(), oldpassword)) {
            throw new AppServiceException("旧密码输入有误！");
        }
        String md5password = encoder.encodeMD5Password(password);
        cu.setPassword(MyPasswordEncoder.extractPassword(md5password));
        cu.setSalt(MyPasswordEncoder.extractSalt(md5password));
        save(cu);
    }

    @Override
    public SecurityUser findByAccountName(String platform, String accountName) {
        return securityUserDao.findByAccountName(platform, accountName);
    }
}
