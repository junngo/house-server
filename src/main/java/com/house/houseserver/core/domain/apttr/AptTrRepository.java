package com.house.houseserver.core.domain.apttr;

import com.house.houseserver.core.domain.apt.Apt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;


public interface AptTrRepository extends JpaRepository<AptTr, Long> {

    Optional<AptTr> findAptTrByAptAndExclusiveAreaAndTrDateAndTrAmountAndFloor(
            Apt apt, Double exclusiveArea, LocalDate trDate, Long trAmount, Integer floor
    );

//    @Query("select ad from AptDeal ad join fetch ad.apt where ad.dealCanceled = 0 and ad.dealDate = ?1")
//    List<AptDeal> findByDealCanceledIsFalseAndDealDateEquals(LocalDate dealDate);
}
