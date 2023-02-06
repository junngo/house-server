package com.house.houseserver.core.dto;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * 아파트 실거래가 데이터 파싱을 위한 DTO
 */
@ToString
@Getter
public class AptTrDto {

    @XmlElement(name = "거래금액")
    private String trAmount;

    /**
     *
     * @return dealAmount - String to Long
     */
    public Long getTrAmount() {
        String amount = trAmount.replace(",", "").trim();
        return Long.parseLong(amount);
    }

    @XmlElement(name = "건축년도")
    private Integer builtYear;

    @XmlElement(name = "년")
    private Integer year;

    @XmlElement(name = "법정동")
    private String dong;

    @XmlElement(name = "아파트")
    private String aptName;

    @XmlElement(name = "월")
    private Integer month;

    @XmlElement(name = "일")
    private Integer day;

    public LocalDate getTrDate() {
        return LocalDate.of(year, month, day);
    }

    @XmlElement(name = "전용면적")
    private Double exclusiveArea;

    @XmlElement(name = "지번")
    private String jibun;

    public String getJibun() {
        return Optional.ofNullable(jibun).orElse("");
    }

    @XmlElement(name = "지역코드")
    private String regionalCode;

    @XmlElement(name = "층")
    private Integer floor;

    @XmlElement(name = "해제사유발생일")
    private String trCanceledDate;    //  21.07.30

    public LocalDate getTrCanceledDate() {
        if (StringUtils.isBlank(trCanceledDate)) {
            return null;
        }
        return LocalDate.parse(trCanceledDate.trim(), DateTimeFormatter.ofPattern("yy.MM.dd"));
    }

    @XmlElement(name = "해제여부")
    private String trCanceled;        // O

    /**
     *
     * @return dealCanceled - String to boolean
     */
    public boolean isTrCanceled() {
        return "O".equals(trCanceled.trim());
    }

}
