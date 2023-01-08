package com.house.houseserver.job.apt;

import com.house.houseserver.adapter.AptApiResource;
import com.house.houseserver.core.dto.AptTrDto;
import com.house.houseserver.job.validator.FilePathValidator;
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
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class AptTrInsertJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job aptTrInsertJob(Step aptTrInsertStep) {
        return jobBuilderFactory.get("aptTrInsertJob")
                .incrementer(new RunIdIncrementer())
                .validator(new FilePathValidator())
                .start(aptTrInsertStep)
                .build();
    }

    @JobScope
    @Bean
    public Step aptTrInsertStep(
            StaxEventItemReader<AptTrDto> aptTrReader,
            ItemWriter<AptTrDto> aptTrWriter
    ) {
        return stepBuilderFactory.get("aptTrInsertStep")
                .<AptTrDto, AptTrDto>chunk(10)
                .reader(aptTrReader)
                .writer(aptTrWriter)
                .build();
    }

    @StepScope
    @Bean
    public StaxEventItemReader<AptTrDto> aptTrReader(
            @Value("#{jobParameters['filePath']}") String filePath,
            Jaxb2Marshaller aptTrDtoMarshaller
    ) {
        return new StaxEventItemReaderBuilder<AptTrDto>()
                .name("aptTrReader")
                .resource(new ClassPathResource(filePath))
                .addFragmentRootElements("item")
                .unmarshaller(aptTrDtoMarshaller)
                .build();
    }

    @StepScope
    @Bean
    public Jaxb2Marshaller aptTrDtoMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(AptTrDto.class);
        return jaxb2Marshaller;
    }

    @StepScope
    @Bean
    public ItemWriter<AptTrDto> aptTrWriter() {
        return items -> {
            items.forEach(System.out::println);
        };
    }
}
