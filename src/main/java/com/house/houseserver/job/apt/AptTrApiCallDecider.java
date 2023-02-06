package com.house.houseserver.job.apt;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import static com.house.houseserver.job.apt.AptTrInsertJobConfig.CONTINUABLE;
import static com.house.houseserver.job.apt.AptTrInsertJobConfig.IS_SEARCH;


@Component
public class AptTrApiCallDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();

        String isSearch = executionContext.getString(IS_SEARCH);
        if (isSearch.equals(CONTINUABLE)) {
            return new FlowExecutionStatus(CONTINUABLE);
        } else {
            return new FlowExecutionStatus(FlowExecutionStatus.COMPLETED.getName());
        }
    }
}
