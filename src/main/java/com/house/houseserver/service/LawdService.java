package com.house.houseserver.service;

import com.house.houseserver.core.domain.lawd.Lawd;
import com.house.houseserver.core.domain.lawd.LawdRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class LawdService {

    private final LawdRepository lawdRepository;

    @Transactional
    public void upsert(Lawd lawd) {
        // 데이터가 존재하면 수정, 없을 때는 생성
        Lawd select_lawd = lawdRepository.findByLawdCode(lawd.getLawdCode())
                .orElseGet(Lawd::new);
        select_lawd.setLawdCode(lawd.getLawdCode());
        select_lawd.setLawdDong((lawd.getLawdDong()));
        select_lawd.setExist(lawd.getExist());
        lawdRepository.save(select_lawd);
    }
}
