package com.multi.tenants.api.model;

import com.multi.tenants.api.constant.ITzBaseConstant;
import com.multi.tenants.api.constant.DatabaseConstant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "group")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Group extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = ITzBaseConstant.ENTITY_STRATEGY_ID)
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @Column(name = "name", unique =  true)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "kind")
    private int kind;

    @Column(name = "is_system_role")
    private Boolean isSystemRole = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = DatabaseConstant.PREFIX_TABLE + "permission_group",
            joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id",
                    referencedColumnName = "id"))
    private List<Permission> permissions = new ArrayList<>();
}
