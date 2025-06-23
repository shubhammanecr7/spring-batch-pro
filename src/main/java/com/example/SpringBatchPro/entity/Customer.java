package com.example.SpringBatchPro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @Column(name = "CUSTOMER_ID", nullable = false)
    private int id;
    @Column(name = "FIRST_NAME", length = 50)
    private String firstName;
    @Column(name = "LAST_NAME", length = 50)
    private String lastName;
    @Column(name = "EMAIL", unique = true, length = 100)
    private String email;
    @Column(name = "GENDER", length = 12)
    private String gender;
    @Column(name = "CONTACT", length = 12)
    private String contactNo;
    @Column(name = "COUNTRY", length = 50)
    private String country;
    @Column(name = "DOB" , length = 30)
    private String dob;
}