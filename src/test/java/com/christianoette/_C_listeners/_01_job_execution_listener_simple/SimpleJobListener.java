package com.christianoette._C_listeners._01_job_execution_listener_simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class SimpleJobListener implements JobExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOG.info("Job {} started", jobExecution.getJobId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LOG.info("Job {} ended with status {}", jobExecution.getJobId(), jobExecution.getStatus());
        jobExecution.setExitStatus(new ExitStatus("COMPLETED", "custom description"));
    }
}
