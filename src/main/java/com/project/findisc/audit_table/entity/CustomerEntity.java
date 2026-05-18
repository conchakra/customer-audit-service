package com.project.findisc.audit_table.entity;

import com.project.findisc.audit_table.listener.AuditListener;
import com.project.findisc.audit_table.enums.CustomerStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@EntityListeners(AuditListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String status;
    private String kyc;
    private String aadhaar;
    private String pan;
    private String photo;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ Customer Lifecycle Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus customerStatus;

    // ✅ Role Tracking Fields
    private String createdBy;
    private String verifiedBy;
    private String approvedBy;

    private LocalDateTime verifiedAt;
    private LocalDateTime approvedAt;

    @Column(length = 1000)
    private String remarks;

    public void setNewStatus(String status) {
        this.status = status;
        if (status != null) {
            this.customerStatus = CustomerStatus.valueOf(status);
        }
    }

    public void setStatus(String status){
        this.status = status;
        if (status != null) {
            this.customerStatus = CustomerStatus.valueOf(status.toUpperCase());
        }
    }
    public void setStatus(CustomerStatus status) {
        this.customerStatus = status;
    }
}
