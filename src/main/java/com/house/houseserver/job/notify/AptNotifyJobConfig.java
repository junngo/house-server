package com.house.houseserver.job.notify;

import com.house.houseserver.core.domain.aptalarm.AptNotifyManager;
import com.house.houseserver.core.domain.aptalarm.AptNotifyManagerRepository;
import com.house.houseserver.core.domain.lawd.LawdRepository;
import com.house.houseserver.core.dto.AptDto;
import com.house.houseserver.core.dto.AptNotifyDto;
import com.house.houseserver.core.service.AptTrService;
import com.house.houseserver.job.validator.TrDateParamValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AptNotifyJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job aptNotifyJob(Step aptNotifyStep) {
        return jobBuilderFactory.get("aptNotifyJob")
                .incrementer(new RunIdIncrementer())
                .validator(new TrDateParamValidator())
                .start(aptNotifyStep)
                .build();
    }

    @JobScope
    @Bean
    public Step aptNotifyStep(
            RepositoryItemReader<AptNotifyManager> aptNotifyRepositoryItemReader,
            ItemProcessor<AptNotifyManager, AptNotifyDto> aptNotifyItemProcessor,
            ItemWriter<AptNotifyDto> aptNotifyItemWriter
    ) {
        return stepBuilderFactory.get("aptNotifyStep")
                .<AptNotifyManager, AptNotifyDto>chunk(10)
                .reader(aptNotifyRepositoryItemReader)
                .processor(aptNotifyItemProcessor)
                .writer(aptNotifyItemWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<AptNotifyManager> aptNotifyRepositoryItemReader(
            AptNotifyManagerRepository aptNotifyManagerRepository
    ) {
        return new RepositoryItemReaderBuilder<AptNotifyManager>()
                .name("aptNotifyRepositoryItemReader")
                .repository(aptNotifyManagerRepository)
                .methodName("findByEnabledIsTrue")
                .pageSize(10)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("aptNotifyManagerId", Sort.Direction.DESC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<AptNotifyManager, AptNotifyDto> aptNotifyItemProcessor(
            @Value("#{jobParameters['trDate']}") String trDate,
            AptTrService aptTrService,
            LawdRepository lawdRepository
    ) {
        return new ItemProcessor<AptNotifyManager, AptNotifyDto>() {
            @Override
            public AptNotifyDto process(AptNotifyManager aptNotify) throws Exception {
                List<AptDto> aptDtoList = aptTrService.findByGuLawdCodeAndTrDate(
                        aptNotify.getGuLawdCode(), LocalDate.parse(trDate)
                );
                if (aptDtoList.isEmpty()) {
                    return null;
                }

                String guName = lawdRepository.findByLawdCode(aptNotify.getGuLawdCode() + "00000")
                        .orElseThrow().getLawdDong();

                return AptNotifyDto.builder()
                        .email(aptNotify.getEmail())
                        .guName(guName)
                        .count(aptDtoList.size())
                        .aptTrs(aptDtoList)
                        .build();
            }
        };
    }

    @StepScope
    @Bean
    public ItemWriter<AptNotifyDto> aptNotifyItemWriter() {
        return new ItemWriter<AptNotifyDto>() {
            @Override
            public void write(List<? extends AptNotifyDto> items) throws Exception {
                items.forEach(item -> System.out.println(item.toMessage()));
            }
        };
    }
}
