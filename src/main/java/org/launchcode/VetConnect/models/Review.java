package org.launchcode.VetConnect.models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;

@Entity
public class Review extends AbstractEntity{

    @NotBlank
    private String reviewText;

    private Integer reviewRating;

    @ManyToOne
    private User user;

    @ManyToOne
    private Clinic clinic;

    public Review() {
    }

    public Review(String reviewText, Integer reviewRating, User user, Clinic clinic) {
        super();
        this.reviewText = reviewText;
        this.reviewRating = reviewRating;
        this.user = user;
        this.clinic = clinic;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Integer getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(Integer reviewRating) {
        this.reviewRating = reviewRating;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }
}
