package org.launchcode.VetConnect.controllers;

import org.launchcode.VetConnect.models.Claim;
import org.launchcode.VetConnect.models.Clinic;
import org.launchcode.VetConnect.models.User;
import org.launchcode.VetConnect.models.data.ClaimRepository;
import org.launchcode.VetConnect.models.data.ClinicRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;

@Controller
public class ClaimController extends VetConnectController{

    @Autowired
    ClaimRepository claimRepository;

    @Autowired
    ClinicRepository clinicRepository;

    @GetMapping(value = "claim-request")
    public String processClaimRequest(@RequestParam Long clinicId, HttpServletRequest request) {
        User user = getUserFromSession(request.getSession(false));
        if(user == null || !(user.getUserType().equals("vet"))) {
            return "redirect:error";
        }

        Optional<Clinic> clinic = clinicRepository.findById(clinicId);

        Claim newClaim = new Claim(clinic.get(), user);
        claimRepository.save(newClaim);

        return "redirect:dashboard";
    }
}
