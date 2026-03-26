package com.omnicharge.recharge.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recharges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Recharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String rechargeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 15)
    private String mobileNumber;

    @Column(nullable = false)
    private Long operatorId;

    @Column(nullable = false, length = 50)
    private String operatorName;

    @Column(nullable = false)
    private Long planId;

    @Column(nullable = false, length = 100)
    private String planName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer validityDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RechargeStatus status;

    @Column(length = 36)
    private String transactionId;

    @Column(length = 200)
    private String failureReason;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    public enum RechargeStatus {
        PENDING, PROCESSING, SUCCESS, FAILED
    }
}
