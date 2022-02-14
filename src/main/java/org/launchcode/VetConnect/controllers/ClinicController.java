package org.launchcode.VetConnect.controllers;

import org.launchcode.VetConnect.models.Clinic;
import org.launchcode.VetConnect.models.Request;
import org.launchcode.VetConnect.models.User;
import org.launchcode.VetConnect.models.data.ClinicRepository;
import org.launchcode.VetConnect.models.data.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class ClinicController extends VetConnectController {

    @Autowired
    ClinicRepository clinicRepository;

    @Autowired
    RequestRepository requestRepository;


    @GetMapping(value = "add")
    public String addAClinicForm(Model model, HttpServletRequest request) {
        if (getUserFromSession(request.getSession()) == null) {
            return "redirect:login";
        }

        model.addAttribute(new Request());

        return "add-a-clinic";
    }

    @PostMapping(value = "add")
    public String addAClinicRequest(@ModelAttribute @Valid Request newRequest, Errors errors, HttpServletRequest request, Model model) {
        if(errors.hasErrors()) {
            return "add-a-clinic";
        }

        Clinic existingClinic = clinicRepository.findByName(newRequest.getName());

        if(existingClinic != null) {
            errors.rejectValue("name", "name.alreadyexists", "A clinic with that name already exists");
            return "add-a-clinic";
        }

        if (newRequest.getEmergency() != null) {
            newRequest.setEmergency("1");
        } else {
            newRequest.setEmergency("0");
        }

        User user = getUserFromSession(request.getSession());
        newRequest.setUser(user);
        newRequest.setStatus("Pending");

        requestRepository.save(newRequest);

        return "redirect:";
    }

}
