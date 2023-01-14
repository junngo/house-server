package com.house.houseserver.core.dto;

import lombok.Builder;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class AptNotifyDto {
    private String email;
    private String guName;
    private Integer count;
    private List<AptDto> aptTrs;

    public String toMessage() {
        DecimalFormat decimalFormat = new DecimalFormat();

        return String.format("%s 아파트 실거래가 알림 \n" +
                "총 %d 거래거 발행 했습니다.\n", guName, count)
                +
                aptTrs.stream()
                        .map(tr -> String.format("- %s : %s원\n", tr.getName(), decimalFormat.format(tr.getPrice())))
                        .collect(Collectors.joining());
    }
}
