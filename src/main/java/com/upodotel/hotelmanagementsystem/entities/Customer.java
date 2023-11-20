package com.upodotel.hotelmanagementsystem.entities;

import java.util.Date;

public class Customer {
    private final int id;
    private final String fullName;
    private final String identityNumber;
    private final String phoneNumber;
    private final Date birthDate;
    private final String description;

    public Customer(int id, String fullName, String identityNumber, String phoneNumber, Date birthDate, String description) {
        this.id = id;
        this.fullName = fullName;
        this.identityNumber = identityNumber;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getDescription() {
        return description;
    }


}
