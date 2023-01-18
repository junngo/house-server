package com.house.houseserver.job.lawd;

import com.house.houseserver.BatchTestConfig;
import com.house.houseserver.core.service.LawdService;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {LawdJobConfig.class, BatchTestConfig.class})
public class LawdJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private LawdService lawdService;

    @Test
    public void success() throws Exception {
        // when
        JobParameters parameters = new JobParameters(
                Maps.newHashMap("filePath", new JobParameter("lawd_code_for_test.txt"))
        );
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);

        // then
        assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
        verify(lawdService, times(5)).upsert(any());
    }

    @Test
    public void file_is_not_exist_fail() throws Exception {
        // when
        JobParameters parameters = new JobParameters(
                Maps.newHashMap("filePath", new JobParameter("file_is_not_exist.txt"))
        );

        // then
        assertThrows(JobParametersInvalidException.class, () -> jobLauncherTestUtils.launchJob(parameters));
        verify(lawdService, never()).upsert(any());
    }
}
