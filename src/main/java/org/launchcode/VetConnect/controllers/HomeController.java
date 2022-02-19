package org.launchcode.VetConnect.controllers;


import org.launchcode.VetConnect.models.ClinicData;
import org.launchcode.VetConnect.models.Review;
import org.launchcode.VetConnect.models.User;
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
    private ReviewRepository reviewRepository;

    @GetMapping(value="")
    public String displayIndex() {
        return "index";
    }


    @GetMapping(value="search-results")
    public String displaySearchResults(Model model, @RequestParam String term)
    {
        if (term.isEmpty())
        {
            model.addAttribute("results_heading", "No search results were found.  Please enter a search term.");
        }
        else
        {
            List<Clinic> results = ClinicData.findClinic(term, clinicRepository.findAll());

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

        if(clinic.isPresent()) {
            List<Review> reviews = clinic.get().getReviews();

            if(!(reviews.isEmpty())) {
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);

                OptionalDouble average = reviews.stream().mapToDouble(a -> a.getReviewRating()).average();
                model.addAttribute("average", df.format(average.getAsDouble()));

            }

            if(!(user == null)) {
                Review review = reviewRepository.findByUserIdAndClinicId(user.getId(), clinicId);

                if(review == null) {
                    model.addAttribute(new Review());
                }
            }

            model.addAttribute("clinic", clinic.get());
        }

        return "clinic-profile";
    }

    @PostMapping("clinic-profile")
    public String addAReviewRequest(@ModelAttribute @Valid Review newReview, @RequestParam Long clinicId, Model model, Errors errors, HttpServletRequest request) {
        if(errors.hasErrors()) {
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
