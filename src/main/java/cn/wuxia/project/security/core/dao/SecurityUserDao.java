/*
 * Created on :2013年8月12日 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.core.dao;

import cn.wuxia.project.security.core.entity.SecurityUser;
import cn.wuxia.project.basic.core.common.BaseCommonDao;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

@Component
public class SecurityUserDao extends BaseCommonDao<SecurityUser, String> {
    public SecurityUser findByAccountName(String platform, String accountName) {
        return findUnique(Restrictions.eq("platform", platform), Restrictions.eq("accountName", accountName));
    }
}
