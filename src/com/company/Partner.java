package com.company;

import java.util.ArrayList;
import java.util.List;

public class Partner {
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    List<String> dates = new ArrayList<>();

    public Partner() {
        this.firstName = "";
        this.lastName = "";
        email = "";
        country = "";
        dates = null;
    }
    public Partner(String firstName, String lastName, String email, String country, List<String> dates) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.dates = dates;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }
}
