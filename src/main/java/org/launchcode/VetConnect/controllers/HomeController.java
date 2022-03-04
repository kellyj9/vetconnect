package org.launchcode.VetConnect.controllers;


import org.launchcode.VetConnect.models.Claim;
import org.launchcode.VetConnect.models.ClinicData;
import org.launchcode.VetConnect.models.Review;
import org.launchcode.VetConnect.models.User;
import org.launchcode.VetConnect.models.data.ClaimRepository;
import org.launchcode.VetConnect.models.data.ClinicRepository;
import org.launchcode.VetConnect.models.Clinic;
import org.launchcode.VetConnect.models.data.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@Controller
public class HomeController extends VetConnectController {

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping(value="")
    public String displayIndex() {
        return "index";
    }


    @GetMapping(value="search-results")
    public String displaySearchResults(Model model, @RequestParam String term, @RequestParam(required = false) String emergency)
    {
        if (term.isEmpty())
        {
            model.addAttribute("results_heading", "No search results were found.  Please enter a search term.");
        }
        else
        {
            List<Clinic> results = new ArrayList<>();

            if ((emergency == null)){
                results = ClinicData.findClinic(term, clinicRepository.findAll());
            }
            else {
                results = ClinicData.findClinic(term, clinicRepository.findAllByEmergency(true));
            }

            if (results.isEmpty()) {
                model.addAttribute("results_heading", "No search results found for " + term + "");
            }
            else {
                // search results were found!
                model.addAttribute("results_heading", "Search results for " + term + "");
                model.addAttribute("clinics", results);
            }
        }
        return "search-results";
    }

    @GetMapping("clinic-profile")
    public String displayClinicProfile(@RequestParam Long clinicId, Model model, HttpServletRequest request)
    {
        Optional<Clinic> clinic = clinicRepository.findById(clinicId);
        User user = getUserFromSession(request.getSession(false));
        Claim claimPending = claimRepository.findByClinicIdAndStatus(clinicId, "pending" );
        Claim claimApproved = claimRepository.findByClinicIdAndStatus(clinicId, "approved" );
        String clinicWebsite;

        if(clinic.isPresent()) {
            List<Review> reviews = clinic.get().getReviews();

            if(!(reviews.isEmpty())) {
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);

                OptionalDouble average = reviews.stream().mapToDouble(a -> a.getReviewRating()).average();
                model.addAttribute("average", df.format(average.getAsDouble()));
                model.addAttribute("totalReviews", reviews.size());
            }

            if(!(user == null)) {
                Review review = reviewRepository.findByUserIdAndClinicId(user.getId(), clinicId);
                model.addAttribute("userType", user.getUserType());

                if(review == null) {
                    model.addAttribute(new Review());
                }
            }

            model.addAttribute("clinic", clinic.get());
        }

        if(claimApproved != null) {
            model.addAttribute("claimApproved", true);

            if(user != null && user.getId() == claimApproved.getUser().getId()) {
                model.addAttribute("vetClaimedClinic", true);
            }
        } else if (claimPending != null) {
            model.addAttribute("claimPending", true);
        } else {
            model.addAttribute("noClaim", true);

        }

        clinicWebsite = clinic.get().getWebsite().replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)","");
        model.addAttribute("clinic", clinic.get());
        model.addAttribute("clinicWebsite", "http://www." + clinicWebsite );

        return "clinic-profile";
    }

    @PostMapping("clinic-profile")
    public String addAReviewRequest(@RequestParam Long clinicId, @ModelAttribute @Valid Review newReview, Errors errors,  HttpServletRequest request, Model model) {
        if(errors.hasErrors()) {
            Optional<Clinic> clinic = clinicRepository.findById(clinicId);

            model.addAttribute("clinic", clinic.get());
            return "clinic-profile";
        }

        Optional<Clinic> clinic = clinicRepository.findById(clinicId);

        if(clinic.isPresent()) {
            User user = getUserFromSession(request.getSession(false));
            newReview.setUser(user);
            newReview.setClinic(clinic.get());
        }

        reviewRepository.save(newReview);


        return "redirect:clinic-profile?clinicId=" + clinicId;
    }



}
