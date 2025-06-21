package com.example.SpringBatchPro.config;

import com.example.SpringBatchPro.entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class CustomerItemProcessor implements ItemProcessor<Customer,Customer> {

    private static final Logger log = LoggerFactory.getLogger(CustomerItemProcessor.class);

    @Override
    public Customer process(Customer customer) throws Exception {

        //logic to process only indian origin customers
        /*if (customer.getCountry().equals("India")){
            return customer;
        }
        return null;*/

        log.info("Processing customer : {}",customer.getFirstName());
        return customer; //do process all customer
    }
}