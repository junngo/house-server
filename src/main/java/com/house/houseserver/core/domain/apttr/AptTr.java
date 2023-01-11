package com.house.houseserver.core.domain.apttr;

import com.house.houseserver.core.domain.apt.Apt;
import com.house.houseserver.core.dto.AptTrDto;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "apt_tr")
@Entity
public class AptTr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aptTrId;

    @ManyToOne
    @JoinColumn(name = "apt_id")
    private Apt apt;

    @Column(nullable = false)
    private Double exclusiveArea;

    @Column(nullable = false)
    private LocalDate trDate;

    @Column(nullable = false)
    private Long trAmount;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private boolean trCanceled;

    @Column
    private LocalDate trCanceledDate;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static AptTr of(AptTrDto dto, Apt apt) {
        AptTr aptTr = new AptTr();
        aptTr.setApt(apt);
        aptTr.setExclusiveArea(dto.getExclusiveArea());
        aptTr.setTrDate(dto.getTrDate());
        aptTr.setTrAmount(dto.getTrAmount());
        aptTr.setFloor(dto.getFloor());
        aptTr.setTrCanceled(dto.isTrCanceled());
        aptTr.setTrCanceledDate(dto.getTrCanceledDate());

        return aptTr;
    }
}
