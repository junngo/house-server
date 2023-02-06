package com.house.houseserver.job.apt;

import com.house.houseserver.core.dto.AptBodyOriginDto;
import com.house.houseserver.core.service.AptTrService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.house.houseserver.job.apt.AptTrInsertJobConfig.*;


@RequiredArgsConstructor
@StepScope
@Component
public class RetrievingItemWriter implements ItemWriter<AptBodyOriginDto> {

    private final AptTrService aptTrService;
    private StepExecution stepExecution;

    @Override
    public void write(List<? extends AptBodyOriginDto> items) throws Exception {
        System.out.println("In write: " + items);
        items.forEach(item -> item.getItems().forEach(aptTrService::upsert));

        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();

        //  리스트지만 데이터는 1개의 객체 데이터만 존재
        Integer numOfRows = items.get(0).getNumOfRows();
        Integer pageNo = items.get(0).getPageNo();
        Integer totalCount = items.get(0).getTotalCount();

        if (numOfRows * pageNo < totalCount) {
            // 페이지의 따른 계속 조회 하는 경우
            executionContext.putString(IS_SEARCH, CONTINUABLE);
            executionContext.putInt(PAGE_NO, pageNo + 1);
        } else {
            executionContext.putInt(PAGE_NO, -1);
            executionContext.putString(IS_SEARCH, FlowExecutionStatus.COMPLETED.getName());
        }
        System.out.println("Finished Writing data");
    }

    @BeforeStep
    public void retrieveInterstepData(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
