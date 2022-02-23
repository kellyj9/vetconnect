package org.launchcode.VetConnect.models;


import org.springframework.format.annotation.NumberFormat;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Clinic extends AbstractEntity{

    @NotBlank(message = "Name required")
    private String name;

    @NotBlank(message = "Address required")
    private String address;

    @NotBlank(message = "City required")
    private String city;

    @NotBlank(message = "State required")
    private String state;

    @NotBlank(message="Zip required")
    @NumberFormat
    @Size(min=5,max=5, message="Must be a valid Zip Code")
    private String zip;

    @NotBlank(message = "Phone Number required")
    @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$",
            message="Must be a valid phone number")
    private String phoneNumber;

    @NotBlank(message = "Website required")
    private String website;

    private String emergency;

    @OneToMany(mappedBy = "clinic")
    private final List<Review> reviews = new ArrayList<>();

    @OneToOne
    private Claim claim;

    public Clinic() {}

//    Initialize the id and value fields
    public Clinic(String name, String phoneNumber, String address, String city, String state, String zip, String website, String emergency) {
        super();
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.website = website;
        this.emergency = emergency;
    }

    //    Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String phoneNumberToString() {return phoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");}

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public List<Review> getReviews() {
        return reviews;
    }
    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }
}
