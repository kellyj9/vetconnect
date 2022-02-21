package org.launchcode.VetConnect.models;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Claim extends AbstractEntity{

    @OneToOne
    private Clinic clinic;

    @ManyToOne
    private User user;

    private String status = "pending";

    @CreationTimestamp
    private Date createdTimestamp;

    public Claim() {}

    public Claim(Clinic clinic, User user) {
        this.clinic = clinic;
        this.user = user;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String approved) {
        this.status = approved;
    }
}
