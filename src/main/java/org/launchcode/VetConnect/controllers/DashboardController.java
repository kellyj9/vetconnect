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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
            return "redirect:dashboard-pet-owner/page/1";
        }
        else if (user.getUserType().equals("vet")) {
                return "redirect:dashboard-vet/page/1";
        }
        else if (user.getUserType().equals("admin")) {
            return "redirect:dashboard-admin/page/1";
        }
        else {
            return "redirect:error";
        }
    }

    @GetMapping(value="dashboard-pet-owner/page/{pageNo}")
    public String displayDashboardPetOwner(Model model, HttpServletRequest request, @PathVariable(value = "pageNo") int pageNo, @RequestParam(required = false) String filter, @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        User this_user = getUserFromSession(request.getSession(false));
        String userType = this_user.getUserType();
        Sort sort = sortDir.equals("asc")  ? Sort.by("createdTimestamp").ascending() : Sort.by("createdTimestamp").descending() ;

        if (!(userType.equals("petOwner"))) {
            return "redirect:error";
        }
        Long thisUserId = this_user.getId();
        if (filter == null) {
            filter = "all";
        }
        int pageSize = 3; // number of records on page
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        List<Request> requests;
        Page<Request> page;
        if (filter.equals("all")) {
            page = requestRepository.findAllByUserId(thisUserId, pageable);
        } else {
            // next two lines can edited in the future if string 'status' is changed to lowercase in db records
            String cap = filter.substring(0, 1).toUpperCase() + filter.substring(1);
            page = requestRepository.findAllByUserIdAndStatus(thisUserId, cap, pageable);
        }
        requests = page.getContent();
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("filter", filter);
        model.addAttribute("userType", userType);
        return "dashboard-pet-owner";
    }

    @GetMapping(value="dashboard-vet/page/{pageNo}")
    public String displayDashboardVet(Model model, HttpServletRequest request, @PathVariable(value = "pageNo") int pageNo, @RequestParam(required = false) String viewType, @RequestParam(required = false) String filter, @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        User this_user = getUserFromSession(request.getSession(false));
        String userType = this_user.getUserType();
        Sort sort = sortDir.equals("asc")  ? Sort.by("createdTimestamp").ascending() : Sort.by("createdTimestamp").descending() ;

        if (!(userType.equals("vet"))) {
            return "redirect:error";
        }
        Long thisUserId = this_user.getId();
        if (viewType == null) {
            viewType = "claims";
        }
        if (filter == null) {
            filter = "approved";
        }
        int pageSize = 3; // number of records on page
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        if (viewType.equals("requests")) {
            List<Request> requests;
            Page<Request> page;
            if (filter.equals("all")) {
                page = requestRepository.findAllByUserId(thisUserId, pageable);
            } else {
                // next two lines can edited in the future if string 'status' is changed to lowercase in db records
                String cap = filter.substring(0, 1).toUpperCase() + filter.substring(1);
                page = requestRepository.findAllByUserIdAndStatus(thisUserId, cap, pageable);
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
                page = claimRepository.findAllByUserId(thisUserId, pageable);
            } else {
                page = claimRepository.findAllByUserIdAndStatus(thisUserId, filter, pageable);
            }
            claims = page.getContent();
            model.addAttribute("claims", claims);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
        }

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("viewType", viewType);
        model.addAttribute("filter", filter);
        model.addAttribute("userType", userType);
        return "dashboard-vet";
    }

    @GetMapping(value="dashboard-admin/page/{pageNo}")
    public String displayDashboardAdmin(Model model, HttpServletRequest request, @PathVariable(value = "pageNo") int pageNo, @RequestParam(required = false) String viewType, @RequestParam(required = false) String filter, @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        User this_user = getUserFromSession(request.getSession(false));
        String userType = this_user.getUserType();
        Sort sort = sortDir.equals("asc")  ? Sort.by("createdTimestamp").ascending() : Sort.by("createdTimestamp").descending() ;

        if (!(userType.equals("admin"))) {
            return "redirect:error";
        }
        if (viewType == null) {
            viewType = "requests";
        }
        if (filter == null) {
            filter = "pending";
        }
        int pageSize = 3; // number of records on page
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

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

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("viewType", viewType);
        model.addAttribute("filter", filter);
        model.addAttribute("userType", userType);
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
