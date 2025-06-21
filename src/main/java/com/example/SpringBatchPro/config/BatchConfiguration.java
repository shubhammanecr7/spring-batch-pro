package com.example.SpringBatchPro.config;

import com.example.SpringBatchPro.entity.Customer;
import com.example.SpringBatchPro.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);
    @Value("${batch.inputFilePath}")
    private String inputFilePath;
    private final CustomerRepository customerRepository;

    public BatchConfiguration(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    //create reader
    @Bean
    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<Customer>();
        reader.setName("csv-reader");
        reader.setResource(new FileSystemResource(inputFilePath)); //csv file path
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());

        return reader;
    }

    private LineMapper<Customer> lineMapper() {
        //Delimited tokenizer,Field set mapper,Default line mapper
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");
        tokenizer.setStrict(false);

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        DefaultLineMapper<Customer> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        defaultLineMapper.setLineTokenizer(tokenizer);

        return defaultLineMapper;
    }

    //create processor
    @Bean
    public CustomerItemProcessor processor() {
        return new CustomerItemProcessor();
    }

    //create writer
    @Bean
    public RepositoryItemWriter<Customer> writer(){
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    //create step
    @Bean
    public Step step1(JobRepository jobRepository , PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    //for Async
    private TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

    //create job
    @Bean
    public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("** Job started now!!");
        return new JobBuilder("importCustomerJob", jobRepository)
                .flow(step1(jobRepository,transactionManager))
                .end()
                .build();
    }
}