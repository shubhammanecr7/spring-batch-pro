package com.example.SpringBatchPro.config;

import com.example.SpringBatchPro.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution){
        if (jobExecution.getStatus() == BatchStatus.COMPLETED){
            log.info("!! JOB FINISHED... Time to verify the results");

            jdbcTemplate
                    .query("""
                            select customer_id,
                                   first_name,
                                   last_name,
                                   email,
                                   gender,
                                   contact,
                                   country,
                                   dob from customer_info""" ,new DataClassRowMapper<>(Customer.class)
                    ).forEach(
                            customer->
                                    log.info("Found <{}> in the database.",customer));
        }
    }

}
