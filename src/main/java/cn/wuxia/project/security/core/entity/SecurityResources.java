package cn.wuxia.project.security.core.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import cn.wuxia.project.security.core.enums.SystemType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import cn.wuxia.project.common.model.ModifyInfoEntity;

@Entity
@Table(name = "security_resources")
@Where(clause = ModifyInfoEntity.ISOBSOLETE_DATE_IS_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SecurityResources extends ModifyInfoEntity {

    private static final long serialVersionUID = 1227390629186486032L;

    private String uri;

    private String description;

    private SystemType type;

    public SecurityResources() {
        super();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Enumerated(EnumType.STRING)
    public SystemType getType() {
        return type;
    }

    public void setType(SystemType type) {
        this.type = type;
    }

}
