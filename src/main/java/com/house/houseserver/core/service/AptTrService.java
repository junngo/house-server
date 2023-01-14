package com.house.houseserver.core.service;

import com.house.houseserver.core.domain.apt.Apt;
import com.house.houseserver.core.domain.apt.AptRepository;
import com.house.houseserver.core.domain.apttr.AptTr;
import com.house.houseserver.core.domain.apttr.AptTrRepository;
import com.house.houseserver.core.dto.AptDto;
import com.house.houseserver.core.dto.AptTrDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<AptDto> findByGuLawdCodeAndTrDate(String guLawdCode, LocalDate trDate) {
        return aptTrRepository.findByTrCanceledIsFalseAndTrDateEquals(trDate)
                .stream()
                .filter(aptTr -> aptTr.getApt().getGuLawdCode().equals(guLawdCode))
                .map(aptTr -> new AptDto(aptTr.getApt().getAptName(), aptTr.getTrAmount()))
                .collect(Collectors.toList());
    }
}
