package com.multi.tenants.api.model;

import com.multi.tenants.api.constant.ITzBaseConstant;
import com.multi.tenants.api.constant.DatabaseConstant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "category")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Category extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = ITzBaseConstant.ENTITY_STRATEGY_ID)
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    private String name;

    @Column(name = "description" ,  columnDefinition = "TEXT")
    private String description;

    private String image;

    private Integer ordering;

    private Integer kind;
}
