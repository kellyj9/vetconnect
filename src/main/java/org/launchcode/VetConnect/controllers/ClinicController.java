package org.launchcode.VetConnect.controllers;

import org.launchcode.VetConnect.models.Claim;
import org.launchcode.VetConnect.models.Clinic;
import org.launchcode.VetConnect.models.Request;
import org.launchcode.VetConnect.models.User;
import org.launchcode.VetConnect.models.data.ClaimRepository;
import org.launchcode.VetConnect.models.data.ClinicRepository;
import org.launchcode.VetConnect.models.data.RequestRepository;
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
public class ClinicController extends VetConnectController {

    @Autowired
    ClinicRepository clinicRepository;

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    ClaimRepository claimRepository;


    @GetMapping(value = "add-a-clinic")
    public String addAClinicForm(Model model, HttpServletRequest request) {

        User this_user = getUserFromSession(request.getSession(false));
        if(this_user == null) {
            return "redirect:login";
        }
        model.addAttribute(new Request());

        return "add-a-clinic";
    }



    @PostMapping(value = "add-a-clinic")
    public String addAClinicRequest(@ModelAttribute @Valid Request newRequest, Errors errors, HttpServletRequest request, Model model) {

        User user = getUserFromSession(request.getSession(false));
        if(user == null) {
            return "redirect:error";
        }

        if(errors.hasErrors()) {
            return "add-a-clinic";
        }

        Clinic existingClinic = clinicRepository.findByName(newRequest.getName());

        if(existingClinic != null) {
            errors.rejectValue("name", "name.alreadyexists", "A clinic with that name already exists");
            return "add-a-clinic";
        }

        newRequest.setUser(user);
        newRequest.setStatus("Pending");
        newRequest.setPhoneNumber(newRequest.getPhoneNumber().replaceAll("[^0-9]",""));


        newRequest.setWebsite(newRequest.getWebsite().replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)",""));

        requestRepository.save(newRequest);

        return "redirect:dashboard";
    }


    @GetMapping(value = "edit-a-clinic")
    public String editClinicForm(Model model, HttpServletRequest request, @RequestParam Long clinicId) {
        User user = getUserFromSession(request.getSession(false));
        Optional<Clinic> clinic = clinicRepository.findById(clinicId);
        Claim claim = claimRepository.findByClinicIdAndStatus(clinicId, "approved");


        if(user == null || claim == null || (user.getId() != claim.getUser().getId())) {
            return "redirect:dashboard";
        }

        if(!clinic.isPresent()) {
            return "redirect:error";
        }

        model.addAttribute("clinic", clinic.get());

        return "edit-a-clinic";
    }

    @PostMapping(value = "edit-a-clinic")
    public String editClinicFormRequest(@ModelAttribute @Valid Clinic clinic, Errors errors, Model model, HttpServletRequest request, @RequestParam Long clinicId) {
        if(errors.hasErrors()) {
            return "edit-a-clinic";
        }
        Optional<Clinic> optionalClinic = clinicRepository.findById(clinicId);

        if(optionalClinic.isPresent()) {
            Clinic tempClinic = optionalClinic.get();

            tempClinic.setName(clinic.getName());
            tempClinic.setAddress(clinic.getAddress());
            tempClinic.setState(clinic.getState());
            tempClinic.setCity(clinic.getCity());
            tempClinic.setZip(clinic.getZip());
            tempClinic.setWebsite(clinic.getWebsite());
            tempClinic.setPhoneNumber(clinic.getPhoneNumber());
            tempClinic.setEmergency(clinic.getEmergency());

            tempClinic.setWebsite(clinic.getWebsite().replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)",""));

            clinicRepository.save(tempClinic);

        } else {
            return "redirect:error";
        }

        return "redirect:dashboard";
    }


}
