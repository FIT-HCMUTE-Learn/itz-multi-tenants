package com.multi.tenants.api.model;

import com.multi.tenants.api.constant.DatabaseConstant;
import com.multi.tenants.api.constant.ITzBaseConstant;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "tenant")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends Auditable<String> {
    @Id
    @Size(max = 30)
    @Column(name = "tenant_id")
    private String tenantId;

    @Size(max = 30)
    @Column(name = "db")
    private String db;

    @Size(max = 30)
    @Column(name = "password")
    private String password;

    @Size(max = 256)
    @Column(name = "url")
    private String url;
}
