package com.christianoette._A_the_basics._03_scopes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;

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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import com.christianoette.testutils.CourseUtilBatchTestConfig;

@SpringBootTest(classes = {StepScopeTest.TestConfig.class, CourseUtilBatchTestConfig.class})
class StepScopeTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void runJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("inputPath", new JobParameter("classpath:files/_A/input.json"))
            .addParameter("outputPath", new JobParameter("output/myOutput.json"))
            .addParameter("chunkSize", new JobParameter(1L))
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
            return jobBuilderFactory.get("myJob")
                .start(readerStep(null))
                .build();
        }

        @Bean
        @JobScope
        public Step readerStep(@Value("#{jobParameters['chunkSize']}") final Integer chunkSize) {
            SimpleStepBuilder<InputData, OutputData> simpleStepBuilder
                = stepBuilderFactory.get("readJsonStep")
                .chunk(chunkSize);

            return simpleStepBuilder.reader(reader(null))
                .processor(processor())
                .writer(writer(null)).build();
        }

        private ItemProcessor<InputData, OutputData> processor() {
            return inputData -> {
                OutputData outputData = new OutputData();
                outputData.outputValue = inputData.value.toUpperCase();
                return outputData;
            };
        }

        @Bean
        @StepScope
        public JsonItemReader<InputData> reader(@Value("#{jobParameters['inputPath']}") final String inputPath) {
            File file;
            try {
                file = ResourceUtils.getFile(inputPath);
            } catch (FileNotFoundException ex) {
                throw new IllegalArgumentException(ex);
            }
            return new JsonItemReaderBuilder<InputData>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(InputData.class))
                .resource(new FileSystemResource(file))
                .name("jsonItemReader")
                .build();
        }

        @Bean
        @StepScope
        public JsonFileItemWriter<OutputData> writer(@Value("#{jobParameters['outputPath']}") String outputPath) {
            Resource outputResource = new FileSystemResource(outputPath);

            return new JsonFileItemWriterBuilder<OutputData>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(outputResource)
                .name("jsonItemWriter")
                .build();
        }

        public static class InputData {
            public String value;

            @Override
            public String toString() {
                return "InputData{" +
                    "value='" + value + '\'' +
                    '}';
            }
        }

        public static class OutputData {
            public String outputValue;

            @Override
            public String toString() {
                return "OutputData{" +
                    "outputValue='" + outputValue + '\'' +
                    '}';
            }
        }
    }

}
