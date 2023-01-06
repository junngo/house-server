package com.house.houseserver.job.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

public class FilePathValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String filePath = parameters.getString("filePath");
        if (!StringUtils.hasText(filePath)) {
            throw new JobParametersInvalidException("filePath가 존재하지 않습니다.");
        }

        Resource resource = new ClassPathResource(filePath);
        if (!resource.exists()) {
            throw new JobParametersInvalidException("filePath가 클래스 path내에 존재하지 않습니다.");
        }
    }
}
