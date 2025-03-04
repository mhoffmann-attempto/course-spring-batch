package com.christianoette._A_the_basics._01_hello_world_application;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = Application.class)
@EntityScan(basePackageClasses = Application.class)
@EnableBatchProcessing
@PropertySource("/context/without-web-context.properties")
public class Application {

	public static void main(String[] args) throws JobInstanceAlreadyCompleteException,
		JobExecutionAlreadyRunningException,
		JobParametersInvalidException,
		JobRestartException, InterruptedException {
		ConfigurableApplicationContext appContext =
			SpringApplication.run(Application.class, args);

		TriggerJobService triggerJobService = appContext.getBean(TriggerJobService.class);
//        Job helloWorldJob = appContext.getBean("helloWorldJob", Job.class);

//        triggerJobService.runJob(helloWorldJob);
		triggerJobService.runJob();
	}

}
