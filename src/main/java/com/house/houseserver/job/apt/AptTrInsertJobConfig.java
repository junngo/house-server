package com.house.houseserver.job.apt;

import com.house.houseserver.adapter.AptApiResource;
import com.house.houseserver.core.domain.lawd.LawdRepository;
import com.house.houseserver.core.dto.AptBodyOriginDto;
import com.house.houseserver.core.dto.AptTrDto;
import com.house.houseserver.job.validator.YearMonthParamValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class AptTrInsertJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final AptApiResource aptApiResource;
    private final LawdRepository lawdRepository;

    // [STEP1] LAWD 코드 조회 후 1개씩 넘겨주기 위한 상수
    public static final String CONTINUABLE = "CONTINUABLE";
    private static final String GU_LAWD_CODE_LIST= "guLawdCodeList";
    private static final String ITEM_COUNT = "itemCount";
    private static final String GU_LAWD_CODE = "guLawdCode";

    // [STEP2] 남은 데이터의 따른 API 요청을 위한 페이지 계산
    public static final String PAGE_NO = "pageNo";
    public static final String IS_SEARCH = "isSearch";

    @Bean
    public Job aptTrInsertJob(
            Step guLawdCodeStep,
            Step aptTrInsertStep,
            AptTrApiCallDecider decider
    ) {
        return jobBuilderFactory.get("aptTrInsertJob")
                .incrementer(new RunIdIncrementer())
                .validator(aptTrJobParamValidator())
                .start(guLawdCodeStep)
                    .on(CONTINUABLE)
                        .to(aptTrInsertStep)
                            .next(decider)
                                .on(CONTINUABLE).to(aptTrInsertStep)
                            .from(decider)
                                .on("*").end()
                        .next(guLawdCodeStep)
                    .from(guLawdCodeStep)
                        .on("*").end()
                .end()
                .build();
    }

    private JobParametersValidator aptTrJobParamValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(
                new YearMonthParamValidator()
        ));
        return validator;
    }

    /**
     * [step1] - 구 코드를 전체 조회하고 다음 step으로 1개씩 넘겨주기
     *
     * @param guLawdCodeTasklet
     * @return
     */
    @JobScope
    @Bean
    public Step guLawdCodeStep(Tasklet guLawdCodeTasklet) {
        return stepBuilderFactory.get("guLawdCodeStep")
                .tasklet(guLawdCodeTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet guLawdCodeTasklet() {
        // todo: tasklet 별도 클래스로 분리 (리팩토링)
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
                ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();

                List<String> guLawdCodeList;
                if (!executionContext.containsKey(GU_LAWD_CODE_LIST)) {
                    guLawdCodeList = lawdRepository.findDistinctGuLawdCode();
                    executionContext.put(GU_LAWD_CODE_LIST, guLawdCodeList);
                    executionContext.putInt(ITEM_COUNT, guLawdCodeList.size());
                } else {
                    guLawdCodeList = (List<String>) executionContext.get(GU_LAWD_CODE_LIST);
                }
                int itemCount = executionContext.getInt(ITEM_COUNT);
                if (itemCount == 0) {
                    contribution.setExitStatus(ExitStatus.COMPLETED);
                    return RepeatStatus.FINISHED;
                }

                itemCount--;
                String guLawdCode = guLawdCodeList.get(itemCount);
                executionContext.putString(GU_LAWD_CODE, guLawdCode);
                executionContext.putInt(ITEM_COUNT, itemCount);

                contribution.setExitStatus(new ExitStatus(CONTINUABLE));
                return RepeatStatus.FINISHED;
            }
        };
    }

    @JobScope
    @Bean
    public Step contextPrintStep(Tasklet contextPrintTasklet) {
        return stepBuilderFactory.get("contextPrintStep")
                .tasklet(contextPrintTasklet)
                .build();
    }
    @StepScope
    @Bean
    public Tasklet contextPrintTasklet(
            @Value("#{jobExecutionContext['guLawdCode']}") String guLawdCode
    ) {
        return (contribution, chunkContext) -> {
            System.out.println("[contextPrintTasklet] guLawdCode = " + guLawdCode);
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * [step2] - 전달 받은 구코드로 실거래 조회
     *
     * @param aptTrReader
     * @param aptTrWriter
     * @return
     */
    @JobScope
    @Bean
    public Step aptTrInsertStep(
            StaxEventItemReader<AptBodyOriginDto> aptTrReader,
            ItemWriter<AptBodyOriginDto> aptTrWriter
    ) {
        return stepBuilderFactory.get("aptTrInsertStep")
                .<AptBodyOriginDto, AptBodyOriginDto>chunk(1)
                .reader(aptTrReader)
                .writer(aptTrWriter)
                .build();
    }

    @StepScope
    @Bean
    public StaxEventItemReader<AptBodyOriginDto> aptTrReader(
            @Value("#{jobExecutionContext['guLawdCode']}") String guLawdCode,
            @Value("#{jobParameters['yearMonth']}") String yearMonth,
            @Value("#{jobExecutionContext['pageNo']}") Integer pageNo,
            Jaxb2Marshaller aptTrDtoMarshaller
    ) {

        // 요청할 페이지 번호 예외처리
        int pageNum;
        if ((pageNo == null) || (pageNo == -1)) {
            pageNum = 1;
        } else {
            pageNum = pageNo;
        }

        return new StaxEventItemReaderBuilder<AptBodyOriginDto>()
                .name("aptTrReader")
                .resource(aptApiResource.getResource(guLawdCode, YearMonth.parse(yearMonth), pageNum))
                .addFragmentRootElements("body")
                .unmarshaller(aptTrDtoMarshaller)
                .build();
    }

    @StepScope
    @Bean
    public Jaxb2Marshaller aptTrDtoMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(AptBodyOriginDto.class, AptTrDto.class);
        return jaxb2Marshaller;
    }

    @Bean
    public ItemWriter<AptBodyOriginDto> aptTrWriter(RetrievingItemWriter itemWriter) {
        return itemWriter;
    }
}