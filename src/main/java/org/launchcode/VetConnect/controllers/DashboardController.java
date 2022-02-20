package org.launchcode.VetConnect.controllers;

import org.launchcode.VetConnect.models.Request;
import org.launchcode.VetConnect.models.User;
import org.launchcode.VetConnect.models.data.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
        else if (user.getUserType().equals("admin")) {
            return "redirect:dashboard-admin/page/1";
        }
        else {
            return "redirect:error";
        }
    }

    @GetMapping(value="dashboard-pet-owner")
    public String displayDashboardPetOwner(Model model, HttpServletRequest request, @RequestParam(required = false) String filter) {

        User this_user = getUserFromSession(request.getSession(false));
        if (!this_user.getUserType().equals("petOwner")) {
            return "redirect:error";
        }

        List<Request> filteredRequests = new ArrayList<>();

        if(filter == null || filter == "all") {
            filteredRequests = requestRepository.findByUserId(this_user.getId());

        } else {
            filteredRequests = requestRepository.findByUserIdAndStatus(this_user.getId(), filter);
        }
        model.addAttribute("requests", filteredRequests);
        model.addAttribute("filter", filter);
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

    @GetMapping(value="dashboard-admin/page/{pageNo}")
    public String displayDashboardAdmin(Model model, HttpServletRequest request, @PathVariable(value = "pageNo") int pageNo, @RequestParam(required = false) String viewType, @RequestParam(required = false) String filter) {

        User this_user = getUserFromSession(request.getSession(false));
        if (!this_user.getUserType().equals("admin")) {
            return "redirect:error";
        }

        if (viewType == null) {
            viewType = "requests";
        }
        if (filter == null) {
            filter = "pending";
        }

        int pageSize = 3; // number of records on page
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        if (viewType.equals("requests")) {
            List<Request> requests;
            Page<Request> page;
            if (filter.equals("all")) {
                page = requestRepository.findAll(pageable);
            } else {
                page = requestRepository.findAllByStatus(filter, pageable);
            }
            requests = page.getContent();
            model.addAttribute("requests", requests);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
        }
        //FOR LATER:
//        else {
//            List<Claim> claims;
//            Page<Claim> page;
//            if (filter.equals("all")) {
//                page =  claimRepository.findAll(pageable);
//            } else {
//                page = claimRepository.findAllByStatus(filter, pageable);
//            }
//            claim = page.getContent();
//            model.addAttribute("claims", claims);
//            model.addAttribute("currentPage", pageNo);
//            model.addAttribute("totalPages", page.getTotalPages());
//            model.addAttribute("totalItems", page.getTotalElements());
//        }

        model.addAttribute("viewType", viewType);
        model.addAttribute("filter", filter);

        return "dashboard-admin";
    }


}
