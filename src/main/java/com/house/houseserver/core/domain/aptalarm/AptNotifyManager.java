package com.house.houseserver.core.domain.aptalarm;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "apt_notify_manager")
@Entity
public class AptNotifyManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aptNotifyManagerId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String guLawdCode;

    @Column(nullable = false)
    private boolean enabled;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
