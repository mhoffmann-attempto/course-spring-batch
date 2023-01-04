package com.christianoette._E_validation_and_fault_tolerance._02_default_validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.christianoette.testutils.CourseUtilBatchTestConfig;
import com.christianoette.utils.CourseUtilJobSummaryListener;

@SpringBootTest(classes = {DefaultValidatorTest.TestConfig.class, CourseUtilBatchTestConfig.class})
class DefaultValidatorTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("parameterOne", new JobParameter(25L))
//            .addParameter("parameterThree", new JobParameter(25L))
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

            String[] requiredKeys = {"parameterOne"};
            String[] optionalKeys = {"parameterTwo"};

            DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator(
                requiredKeys, optionalKeys
            );

            return jobBuilderFactory.get("myJob")
                .start(stepOne())
                .validator(defaultJobParametersValidator)
                .listener(new CourseUtilJobSummaryListener())
                .build();
        }

        @Bean
        @JobScope
        public Step stepOne() {
            return stepBuilderFactory.get("dummyStep")
                .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
        }
    }

}
