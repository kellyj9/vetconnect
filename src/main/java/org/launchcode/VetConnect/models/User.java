package org.launchcode.VetConnect.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class User extends AbstractEntity {

    @NotBlank
    private String userType;

    @Size(min = 2, max = 26)
    @NotBlank
    private String firstName;

    @Size(min = 2, max = 26)
    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String emailAddress;

    @NotBlank
    private String pwHash;

    @OneToMany(mappedBy = "user")
    private final List<Request> requests = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private final List<Claim> claims = new ArrayList<>();

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User() {}

    public User(String userType, String firstName, String lastName, String emailAddress, String password) {
        super();
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.pwHash = encoder.encode(password);
    }

    public List<Claim> getClaims() {
        return claims;
    }
    
    public List<Request> getRequests() {
        return requests;
    }

    public String getUserType() {
        return userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public boolean isMatchingPassword(String password) {
        return encoder.matches(password, pwHash);
    }

}
