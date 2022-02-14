package org.launchcode.VetConnect.controllers;

import org.launchcode.VetConnect.models.User;
import org.launchcode.VetConnect.models.data.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DashboardController extends VetConnectController{

    @Autowired
    RequestRepository requestRepository;

    @GetMapping(value="dashboard")
    public String getDashboard(Model model, HttpServletRequest request) {
        User user = getUserFromSession(request.getSession(false));

        if (user.getUserType().equals("petOwner")) {
            return "redirect:dashboard-pet-owner";
        }
        else if (user.getUserType().equals("vet")) {
                return "redirect:dashboard-vet";
        }
        else {
            return "redirect:error";
        }
    }

    @GetMapping(value="dashboard-pet-owner")
    public String displayDashboardPetOwner(Model model, HttpServletRequest request) {
        User this_user = getUserFromSession(request.getSession(false));
        if (!this_user.getUserType().equals("petOwner")) {
            return "redirect:error";
        }
        model.addAttribute("requests", this_user.getRequests());
        return "dashboard-pet-owner";
    }

    @GetMapping(value="dashboard-vet")
    public String displayDashboardVet(Model model, HttpServletRequest request) {
        User this_user = getUserFromSession(request.getSession(false));
        if (!this_user.getUserType().equals("vet")) {
            return "redirect:error";
        }
        model.addAttribute("requests", this_user.getRequests());
        return "dashboard-vet";
    }

}
