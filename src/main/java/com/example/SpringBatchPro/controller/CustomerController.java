package com.example.SpringBatchPro.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;
    private final static Logger log = LoggerFactory.getLogger(CustomerController.class);

    @GetMapping("/startBatch")
    public BatchStatus loadCsvDataToDB() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()) // ensures uniqueness
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        log.info("------------------Job started successfully!");
        return jobExecution.getStatus();
    }


}