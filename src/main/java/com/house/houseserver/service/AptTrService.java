package com.house.houseserver.service;

import com.house.houseserver.core.domain.apt.Apt;
import com.house.houseserver.core.domain.apt.AptRepository;
import com.house.houseserver.core.domain.apttr.AptTr;
import com.house.houseserver.core.domain.apttr.AptTrRepository;
import com.house.houseserver.core.dto.AptTrDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AptTrService {

    private final AptRepository aptRepository;
    private final AptTrRepository aptTrRepository;

    @Transactional
    public void upsert(AptTrDto dto) {
        Apt apt = getAptOrNew(dto);
        saveAptTr(dto, apt);
    }

    private Apt getAptOrNew(AptTrDto dto) {
        Apt apt = aptRepository.findAptByAptNameAndJibun(dto.getAptName(), dto.getJibun())
                .orElseGet(() -> Apt.from(dto));
        return aptRepository.save(apt);
    }

    private void saveAptTr(AptTrDto dto, Apt apt) {
        AptTr aptTr = aptTrRepository.findAptTrByAptAndExclusiveAreaAndTrDateAndTrAmountAndFloor(
                apt, dto.getExclusiveArea(), dto.getTrDate(), dto.getTrAmount(), dto.getFloor()
        ).orElseGet(() -> AptTr.of(dto, apt));

        aptTr.setTrCanceled(dto.isTrCanceled());
        aptTr.setTrCanceledDate(dto.getTrCanceledDate());
        aptTrRepository.save(aptTr);
    }
}
