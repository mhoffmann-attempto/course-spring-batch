package com.christianoette._A_the_basics._05_steps_in_separate_files;

import static javax.print.attribute.standard.JobState.COMPLETED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.in;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.christianoette._A_the_basics._05_steps_in_separate_files.config.BatchConfig;
import com.christianoette._A_the_basics._05_steps_in_separate_files.config.ReaderConfiguration;
import com.christianoette._A_the_basics._05_steps_in_separate_files.dto.InputData;
import com.christianoette._A_the_basics._05_steps_in_separate_files.processor.UpperCaseJsonProcessor;

@SpringBootTest(classes = {
    BatchConfig.class,
    UpperCaseJsonProcessor.class,
    SeparateFilesTest.TestConfig.class})
@EnableBatchProcessing
class SeparateFilesTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void testJob() throws Exception {

        InputData inputData = new InputData();
        inputData.value = "My test data with in Memory";

        TestConfig.inputData.clear();
        TestConfig.inputData.add(inputData);

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addParameter("outputPath", new JobParameter("output/output.json"));
        JobParameters jobParams = jobParametersBuilder.toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParams);
        BatchStatus exitStatus = jobExecution.getStatus();
        assertThat(exitStatus).isEqualTo(BatchStatus.COMPLETED);
    }

    @Configuration
    static class TestConfig {

        static LinkedList<InputData> inputData = new LinkedList<>();

        @Bean
        public JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }

        @Bean
        public ItemReader<InputData> itemReader(){
            return () -> inputData.pollFirst();
        }

    }
}
