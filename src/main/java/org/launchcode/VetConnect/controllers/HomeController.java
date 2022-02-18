package org.launchcode.VetConnect.controllers;


import org.launchcode.VetConnect.models.ClinicData;
import org.launchcode.VetConnect.models.Review;
import org.launchcode.VetConnect.models.data.ClinicRepository;
import org.launchcode.VetConnect.models.Clinic;
import org.launchcode.VetConnect.models.data.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController extends VetConnectController {

    @Autowired
    private ClinicRepository clinicRepository;

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
    public String displayClinicProfile(@RequestParam Long clinicId, Model model)
    {
        Optional<Clinic> clinic = clinicRepository.findById(clinicId);

        if(clinic.isPresent()) {
            model.addAttribute("clinic", clinic.get());
        }

        return "clinic-profile";
    }



}
