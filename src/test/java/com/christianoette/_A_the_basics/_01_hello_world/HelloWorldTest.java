package com.christianoette._A_the_basics._01_hello_world;


import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = HelloWorldTest.TestConfig.class)
class HelloWorldTest {

    private static final String JOB_PARAMETER_OUTPUT_TEXT = "outputText";
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void test() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addParameter(JOB_PARAMETER_OUTPUT_TEXT,
                new JobParameter("Hello Spring Batch"))
            .toJobParameters();

        jobLauncherTestUtils.launchJob(jobParameters);
    }

    @Configuration
    @EnableBatchProcessing
    static class TestConfig {

        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private StepBuilderFactory stepBuilderFactory;

        @Bean
        public Job helloWorldJob() {
            final Step step = stepBuilderFactory.get("step")
                .tasklet((stepContribution, chunkContext) -> {
                    Map<String, Object> jobParameters = chunkContext.getStepContext()
                        .getJobParameters();
                    Object outputText = jobParameters.get(JOB_PARAMETER_OUTPUT_TEXT);
                    System.out.println(outputText);
                    return RepeatStatus.FINISHED;
                }).build();
            return jobBuilderFactory.get("helloWorldJob")
                .start(step)
                .build();
        }

        @Bean
        public JobLauncherTestUtils utils() {
            return new JobLauncherTestUtils();
        }
    }
}
