package com.christianoette._A_the_basics._05_steps_in_separate_files;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.christianoette._A_the_basics._05_steps_in_separate_files.config.BatchConfig;
import com.christianoette._A_the_basics._05_steps_in_separate_files.dto.InputData;
import com.christianoette._A_the_basics._05_steps_in_separate_files.processor.UpperCaseJsonProcessor;

@SpringBootTest(classes = {
    BatchConfig.class,
    UpperCaseJsonProcessor.class,
    MockitoSeparateFilesTest.TestConfig.class})
@EnableBatchProcessing
class MockitoSeparateFilesTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @MockBean
    private ItemReader<InputData> itemReader;

    @Test
    void testJob() throws Exception {
        InputData inputData = new InputData();
        inputData.value = "My test data with Mockito in Memory";

        doReturn(inputData, inputData, null)
            .when(itemReader).read();

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addParameter("outputPath", new JobParameter("output/output.json"));
        JobParameters jobParams = jobParametersBuilder.toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);
        BatchStatus exitStatus = jobExecution.getStatus();
        assertThat(exitStatus).isEqualTo(BatchStatus.COMPLETED);
    }

    @Configuration
    static class TestConfig {

        @Bean
        public JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }

    }
}
