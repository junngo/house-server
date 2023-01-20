package com.house.houseserver.job.apt;


import com.house.houseserver.BatchTestConfig;
import com.house.houseserver.adapter.AptApiResource;
import com.house.houseserver.core.domain.lawd.LawdRepository;
import com.house.houseserver.core.service.AptTrService;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {AptTrInsertJobConfig.class, BatchTestConfig.class})
public class AptTrInsertJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private AptTrService aptTrService;

    @MockBean
    private LawdRepository lawdRepository;

    @MockBean
    private AptApiResource aptApiResource;

    @DisplayName("정상 성공")
    @Test
    public void success() throws Exception {
        // when
        when(lawdRepository.findDistinctGuLawdCode()).thenReturn(Arrays.asList("11110"));
        // 실제는 Api로 받은 Resource, 테스트는 파일로 읽은 Resource
        when(aptApiResource.getResource(anyString(), any())).thenReturn(
                new ClassPathResource("apt_api_response_sample.xml")
        );

        // given
        JobExecution execution = jobLauncherTestUtils.launchJob(
                new JobParameters(Maps.newHashMap("yearMonth", new JobParameter("2023-01")))
        );

        // then
        assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        verify(aptTrService, times(8)).upsert(any());
    }

    /**
     * lawd 코드가 여러개인 경우 lawd 코드만큼 2번째 step 실행
     * lawd 코드 11110과 41590 2개를 입력 -> 2번이 실행이 되어 upsert 코드 2x 실행
     *
     * @throws Exception
     */
    @DisplayName("lawd 코드가 여러개인 경우")
    @Test
    public void multiple_lawd_success() throws Exception {
        // when
        when(lawdRepository.findDistinctGuLawdCode()).thenReturn(Arrays.asList("11110", "41590"));
        // 실제는 Api로 받은 Resource, 테스트는 파일로 읽은 Resource
        when(aptApiResource.getResource(anyString(), any())).thenReturn(
                new ClassPathResource("apt_api_response_sample.xml")
        );

        // given
        JobExecution execution = jobLauncherTestUtils.launchJob(
                new JobParameters(Maps.newHashMap("yearMonth", new JobParameter("2022-12")))
        );

        // then
        assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        verify(aptTrService, times(16)).upsert(any());
    }

    @DisplayName("년월 파라미터가 없는 경우")
    @Test
    public void yearMonth_is_not_exist_fail() {
        // given
        when(lawdRepository.findDistinctGuLawdCode()).thenReturn(Arrays.asList("11110"));
        // 실제는 Api로 받은 Resource, 테스트는 파일로 읽은 Resource
        when(aptApiResource.getResource(anyString(), any())).thenReturn(
                new ClassPathResource("apt_api_response_sample.xml")
        );

        // when
        assertThrows(JobParametersInvalidException.class,
                () -> jobLauncherTestUtils.launchJob());

        // then
        verify(aptTrService, never()).upsert(any());
    }
}
