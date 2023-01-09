package com.house.houseserver.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class LawdCodeParamValidator implements JobParametersValidator {

    private static final String LAWD_CODE = "lawdCode";

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String lawdCode = parameters.getString(LAWD_CODE);
        if (isNotValid(lawdCode)) {
            throw new JobParametersInvalidException(LAWD_CODE + "은 5자리입니다.");
        }
    }

    private boolean isNotValid(String lawdCode) {
        return !isValid(lawdCode);
    }

    private boolean isValid(String lawdCode) {
        return StringUtils.hasText(lawdCode) && lawdCode.length() == 5;
    }
}
