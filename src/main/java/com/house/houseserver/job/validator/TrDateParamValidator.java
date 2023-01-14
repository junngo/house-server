package com.house.houseserver.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class TrDateParamValidator implements JobParametersValidator {

    private static final String TR_DATE = "trDate";

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String trDate = parameters.getString(TR_DATE);
        if (!StringUtils.hasText(trDate)) {
            throw new JobParametersInvalidException(TR_DATE + "가 비어있거나 존재하지 않습니다.");
        }
        try {
            LocalDate.parse(trDate);
        } catch (DateTimeParseException e) {
            throw new JobParametersInvalidException(TR_DATE + "가 올바른 날짜 형식이 아닙니다. 날짜 형식은 yyyy-MM-dd 입니다.");
        }
    }
}
