package cn.wuxia.project.security.handler.service;

import cn.wuxia.project.security.handler.bean.UserLoginedData;
import cn.wuxia.project.security.common.MyUserDetails;

/**
 * 定义登录时使用的接口类
 * @author songlin
 * @ Version : V<Ver.No> <2017年5月10日>
 */
public interface UserLoginedService {

    /**
     * 验证密码
     * @author songlin
     * @param accountName
     * @param password
     * @return
     */
    public boolean validatePassword(String accountName, String password);

    /**
     * 登录之前操作
     * @author songlin
     */
    public UserLoginedData loginByAccountname(String accountName);

    /**
     * 登录之前操作
     * @author songlin
     */
    public UserLoginedData loginByCasUserid(String casUserid);

    /**
     * 登录之后的操作
     * @author songlin
     */
    public MyUserDetails afterLogin(UserLoginedData loginedDataBean, MyUserDetails userDetail);
}
