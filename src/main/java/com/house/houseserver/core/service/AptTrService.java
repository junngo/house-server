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

        // todo: 취소 건 데이터 클랜징 필요
//        1. 동일 건 2건이 (취소 -> 계약) 순서로 들어 오는 경우가 존재하여, 계약 건은 저장 불필요(지금은 계약된 건으로 저장됨)
//        2. 선 계약 1건이 아닌, 그냥 취소 1건으로 들어 오는 경우는 존재하는가? 그럼 아래 로직 사용시 누락 발생
//        if (aptTr.isTrCanceled()) return;

        aptTr.setTrCanceled(dto.isTrCanceled());
        aptTr.setTrCanceledDate(dto.getTrCanceledDate());
        System.out.println("APT TR: " + aptTr.toString());
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
