/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.service;

import cn.wuxia.project.security.core.entity.SecurityUser;
import cn.wuxia.project.common.service.CommonService;

public interface SecurityUserService extends CommonService<SecurityUser, String> {

    /**
     * 修改密码
     *
     * @param userid
     * @param password
     * @author songlin
     */
    public void updatePasswd(String userid, String password);


    /**
     * 修改密码
     *
     * @param userid
     * @param password
     * @param oldpassword
     * @author songlin
     */
    public void updatePasswd(String userid, String password, String oldpassword);


    public SecurityUser findByAccountName(String platform, String accountName);
}
