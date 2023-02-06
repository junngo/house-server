package com.house.houseserver.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 아파트 실 거래가 API를 호출을 위한 필요 파라미터
 * 1. serviceKey - API를 호출하기 위한 인증키
 * 2. LAWD_CODE - 법정동 코드 10자리 중 앞 5자리 - 구 지역 코드 guLawdCd, 예) 41590
 * 3. TR_YM - 거래가 발생한 년월, 예) 202301
 */
@Slf4j
@Component
public class AptApiResource {

    @Value("${external.apartment-api.path}")
    private String path;

    @Value("${external.apartment-api.service-key}")
    private String serviceKey;

    public Resource getResource(String lawdCode, YearMonth yearMonth, int pageNum) {

        String url = String.format("%s?serviceKey=%s&LAWD_CD=%s&DEAL_YMD=%s&pageNo=%s&numOfRows=300"
                , path
                , serviceKey
                , lawdCode
                , yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM"))
                , pageNum
        );

        log.info("Resource URL = " + url);

        try {
            return new UrlResource(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Failed to created UrlResource");
        }
    }
}
