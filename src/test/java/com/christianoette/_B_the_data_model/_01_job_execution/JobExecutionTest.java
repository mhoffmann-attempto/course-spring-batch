package com.christianoette._B_the_data_model._01_job_execution;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.christianoette.testutils.CourseUtilBatchTestConfig;
import com.christianoette.utils.CourseUtilJobSummaryListener;

@SpringBootTest(classes = {JobExecutionTest.TestConfig.class, CourseUtilBatchTestConfig.class})
class JobExecutionTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @SuppressWarnings("WeakerAccess")
    @Configuration
    static class TestConfig {

        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private StepBuilderFactory stepBuilderFactory;

        @Bean
        public Job job() {
            Job myJob = jobBuilderFactory.get("myJob")
                .start(stepOne())
                .listener(new CourseUtilJobSummaryListener())
                .next(stepTwo())
                .build();
            return myJob;
        }

        @Bean
        @JobScope
        public Step stepOne() {
            return stepBuilderFactory.get("myFirstStep")
                .tasklet((stepContribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                })
                .build();
        }

        @Bean
        @JobScope
        public Step stepTwo() {
            return stepBuilderFactory.get("mySecondStep")
                .tasklet((stepContribution, chunkContext) -> {
                    throw new RuntimeException("Simulate error");
//                    return RepeatStatus.FINISHED;
                })
                .build();
        }
    }

}
