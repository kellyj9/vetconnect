package org.launchcode.VetConnect.controllers;

import org.launchcode.VetConnect.models.Clinic;
import org.launchcode.VetConnect.models.data.ClinicRepository;
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
public class ClinicController {

    @Autowired
    ClinicRepository clinicRepository;

    @GetMapping(value = "add")
    public String addAClinicForm(Model model) {
        model.addAttribute(new Clinic());

        return "add-a-clinic";
    }

    @PostMapping(value = "add")
    public String addAClinicRequest(@ModelAttribute @Valid Clinic newClinic, Errors errors, HttpServletRequest request, Model model) {
        if(errors.hasErrors()) {
            return "add-a-clinic";
        }

        Clinic existingClinic = clinicRepository.findByName(newClinic.getName());

        if(existingClinic != null) {
            errors.rejectValue("name", "name.alreadyexists", "A clinic with that name already exists");
            return "add-a-clinic";
        }


        if (newClinic.getEmergency() != null) {
            newClinic.setEmergency("1");
        } else {
            newClinic.setEmergency("0");
        }

        clinicRepository.save(newClinic);

        return "redirect:";
    }

}
