package com.house.houseserver.core.domain.aptalarm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AptNotifyManagerRepository extends JpaRepository<AptNotifyManager, Long> {

    Page<AptNotifyManager> findByEnabledIsTrue(Pageable pageable);
}
