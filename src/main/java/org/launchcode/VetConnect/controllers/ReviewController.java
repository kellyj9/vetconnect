package org.launchcode.VetConnect.controllers;

import org.launchcode.VetConnect.models.Clinic;
import org.launchcode.VetConnect.models.Review;
import org.launchcode.VetConnect.models.User;
import org.launchcode.VetConnect.models.data.ClinicRepository;
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
import java.util.Optional;

@Controller
public class ReviewController extends VetConnectController {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ClinicRepository clinicRepository;

    @GetMapping("add-a-review")
    public String displayReviewForm(@RequestParam Long clinicId, Model model) {
        Optional<Clinic> clinic = clinicRepository.findById(clinicId);

        if(clinic.isPresent()) {
            model.addAttribute("clinic", clinic.get());
            model.addAttribute(new Review());
        }

        return "add-a-review";
    }

    @PostMapping("add-a-review")
    public String addAReviewRequest(@ModelAttribute @Valid Review newReview, @RequestParam Long clinicId, Model model, Errors errors, HttpServletRequest request) {
        if(errors.hasErrors()) {
            return "add-a-review";
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
