package com.house.houseserver.core.domain.apttr;

import com.house.houseserver.core.domain.apt.Apt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface AptTrRepository extends JpaRepository<AptTr, Long> {

    Optional<AptTr> findAptTrByAptAndExclusiveAreaAndTrDateAndTrAmountAndFloor(
            Apt apt, Double exclusiveArea, LocalDate trDate, Long trAmount, Integer floor
    );

//    @Query("select at from AptTr at join fetch at.apt where at.trCanceled = 0 and at.trDate = ?1")
    @Query("select at from AptTr at join fetch at.apt where at.trCanceled = 0")
    List<AptTr> findByTrCanceledIsFalseAndTrDateEquals(LocalDate trDate);
}
