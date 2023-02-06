package com.house.houseserver.core.domain.lawd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LawdRepository extends JpaRepository<Lawd, Long> {
    Optional<Lawd> findByLawdCode(String lawdCode);

//    @Query("select distinct substring(l.lawdCode, 1, 5) from Lawd l where substring(l.lawdCode, 1, 5) in ('41590')")
    @Query("select distinct substring(l.lawdCode, 1, 5) from Lawd l where l.exist = 1 and l.lawdCode not like '%00000000'")
    List<String> findDistinctGuLawdCode();
}
