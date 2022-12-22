package com.christianoette._A_the_basics._01_hello_world_application;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;

@Component
public class TriggerJobService {

    private static final String JOB_PARAMETER_OUTPUT_TEXT = "outputText";

    private final JobLauncher jobLauncher;

    private final Job job;

    public TriggerJobService(JobLauncher jobLauncher, Job otherJob) {
        this.jobLauncher = jobLauncher;
        this.job = otherJob;
    }

    public void runJob() throws JobInstanceAlreadyCompleteException,
        JobExecutionAlreadyRunningException,
        JobParametersInvalidException,
        JobRestartException, InterruptedException {

        JobParameters jobParameters = new JobParametersBuilder()
            .addParameter(JOB_PARAMETER_OUTPUT_TEXT, new JobParameter("My first spring boot app"))
            .toJobParameters();

        jobLauncher.run(job, jobParameters);

//        Thread.sleep(3000);
//
//        jobLauncher.run(job, jobParameters);

    }
}
