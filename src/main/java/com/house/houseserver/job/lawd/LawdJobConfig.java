package com.house.houseserver.job.lawd;

import com.house.houseserver.core.domain.lawd.Lawd;
import com.house.houseserver.job.validator.FilePathValidator;
import com.house.houseserver.service.LawdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static com.house.houseserver.job.lawd.LawdFieldSetMapper.*;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class LawdJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final LawdService lawdService;

    @Bean
    public Job lawdJob(Step lawdStep) {
        return jobBuilderFactory.get("lawdJob")
                .incrementer(new RunIdIncrementer())
                .validator(new FilePathValidator())
                .start(lawdStep)
                .build();
    }

    @Bean
    @JobScope
    public Step lawdStep(FlatFileItemReader<Lawd> lawdFlatFileItemReader,
                         ItemWriter<Lawd> lawdItemWriter) {
        return stepBuilderFactory.get("lawdStep")
                .<Lawd, Lawd>chunk(1000)
                .reader(lawdFlatFileItemReader)
                .writer(lawdItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Lawd> lawdItemReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<Lawd>()
                .name("lawdItemReader")
                .encoding("x-windows-949")
                .delimited()
                .delimiter("\t")
                .names(LAWD_CODE, LAWD_DONG, EXIST)
                .linesToSkip(1)
                .fieldSetMapper(new LawdFieldSetMapper())
                .resource(new ClassPathResource(filePath))
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<Lawd> lawdItemWriter() {
        return new ItemWriter<Lawd>() {
            @Override
            public void write(List<? extends Lawd> items) throws Exception {
                items.forEach(lawdService::upsert);
            }
        };
    }
}
