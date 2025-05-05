package com.multi.tenants.api.model;

import com.multi.tenants.api.constant.DatabaseConstant;
import com.multi.tenants.api.constant.ITzBaseConstant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "migration_status")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MigrationStatus extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = ITzBaseConstant.ENTITY_STRATEGY_ID)
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id")
    private Tenant tenant;

    @Column(name = "migration_status", nullable = false)
    private String migrationStatus; // PENDING, IN_PROGRESS, COMPLETED, FAILED

    @Column
    private String errorMessage;

    @Column(nullable = false)
    private Date startTime;

    @Column
    private Date endTime;

    @Column
    private String changelogVersion;

    @Column
    private String rollbackVersion;

    @Column
    private Boolean isSuccess;
}