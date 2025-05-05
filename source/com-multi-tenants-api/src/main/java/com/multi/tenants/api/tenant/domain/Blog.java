package com.multi.tenants.api.tenant.domain;

import com.multi.tenants.api.constant.ITzBaseConstant;
import com.multi.tenants.api.constant.DatabaseConstant;
import com.multi.tenants.api.model.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "blog")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Blog extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = ITzBaseConstant.ENTITY_STRATEGY_ID)
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "author", nullable = false)
    private String author;
}
