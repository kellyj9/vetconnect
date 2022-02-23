package org.launchcode.VetConnect.controllers;

import org.launchcode.VetConnect.models.Claim;
import org.launchcode.VetConnect.models.Clinic;
import org.launchcode.VetConnect.models.Request;
import org.launchcode.VetConnect.models.User;
import org.launchcode.VetConnect.models.data.ClaimRepository;
import org.launchcode.VetConnect.models.data.ClinicRepository;

import org.launchcode.VetConnect.models.data.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Controller
public class DashboardController extends VetConnectController{

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    ClaimRepository claimRepository;
   
   @Autowired
    ClinicRepository clinicRepository;

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

        model.addAttribute("approvedClaims", this_user.getClaims().stream().filter(claim -> claim.getStatus().equals("approved")).collect(Collectors.toList()));
        model.addAttribute("claims", this_user.getClaims());
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
      else if (viewType.equals("claims")) {
            List<Claim> claims;
            Page<Claim> page;
            if (filter.equals("all")) {
                page = (Page<Claim>) claimRepository.findAll(pageable);
            } else {
                page = claimRepository.findAllByStatus(filter, pageable);
            }
            claims = page.getContent();
            model.addAttribute("claims", claims);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
        }

        model.addAttribute("viewType", viewType);
        model.addAttribute("filter", filter);

        return "dashboard-admin";
    }

    @PostMapping("admin-request-approve")
    public String processApproveClinicAddRequest(Model model, HttpServletRequest request, @RequestParam String filter, @RequestParam Long requestId) {
        User this_user = getUserFromSession(request.getSession(false));
        if (!this_user.getUserType().equals("admin")) {
            return "redirect:error";
        }

        // make sure the requestId in the url corresponds with an existing record
        Optional<Request> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isPresent()) {
            Request requestTmp = optionalRequest.get();
            requestTmp.setStatus("Approved");
            requestRepository.save(requestTmp);

            Clinic newClinic = new Clinic(requestTmp.getName(), requestTmp.getPhoneNumber(), requestTmp.getAddress(), requestTmp.getCity(), requestTmp.getState(), requestTmp.getZip(), requestTmp.getWebsite(), requestTmp.getEmergency());
            clinicRepository.save(newClinic);
        }
        else {
            return "redirect:error";
        }
        return "redirect:dashboard-admin/page/1?viewType=requests&filter=" + filter;
    }

    @PostMapping("admin-request-decline")
    public String processDeclineClinicAddRequest(Model model, HttpServletRequest request, @RequestParam String filter, @RequestParam Long requestId) {
        User this_user = getUserFromSession(request.getSession(false));
        if (!this_user.getUserType().equals("admin")) {
            return "redirect:error";
        }

        // make sure the requestId in the url corresponds with an existing record
        Optional<Request> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isPresent()) {
            Request requestTmp = optionalRequest.get();
            requestTmp.setStatus("Declined");
            requestRepository.save(requestTmp);
        }
        else {
            return "redirect:error";
        }
        return "redirect:dashboard-admin/page/1?viewType=requests&filter=" + filter;
    }

    @PostMapping("admin-claim-approve")
    public String processApproveClinicClaimRequest(Model model, HttpServletRequest request, @RequestParam String filter, @RequestParam Long claimId) {
        User this_user = getUserFromSession(request.getSession(false));
        if (!this_user.getUserType().equals("admin")) {
            return "redirect:error";
        }

        // make sure the requestId in the url corresponds with an existing record
        Optional<Claim> optionalClaim = claimRepository.findById(claimId);
        if (optionalClaim.isPresent()) {
            Claim claimTmp = optionalClaim.get();
            claimTmp.setStatus("approved");
            claimRepository.save(claimTmp);
        }
        else {
            return "redirect:error";
        }
        return "redirect:dashboard-admin/page/1?viewType=claims&filter=" + filter;
    }

    @PostMapping("admin-claim-decline")
    public String processDeclineClinicClaimRequest(Model model, HttpServletRequest request, @RequestParam String filter, @RequestParam Long claimId) {
        User this_user = getUserFromSession(request.getSession(false));
        if (!this_user.getUserType().equals("admin")) {
            return "redirect:error";
        }

        // make sure the requestId in the url corresponds with an existing record
        Optional<Claim> optionalClaim = claimRepository.findById(claimId);
        if (optionalClaim.isPresent()) {
            Claim claimTmp = optionalClaim.get();
            claimTmp.setStatus("declined");
            claimRepository.save(claimTmp);
        }
        else {
            return "redirect:error";
        }
        return "redirect:dashboard-admin/page/1?viewType=claims&filter=" + filter;
    }

}
