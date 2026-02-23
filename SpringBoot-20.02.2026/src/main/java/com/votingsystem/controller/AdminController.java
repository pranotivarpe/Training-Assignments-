package com.votingsystem.controller;

import com.votingsystem.dto.CandidateDto;
import com.votingsystem.dto.ElectionDto;
import com.votingsystem.model.Candidate;
import com.votingsystem.model.Election;
import com.votingsystem.repository.CandidateRepository;
import com.votingsystem.service.ElectionService;
import com.votingsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ElectionService electionService;
    private final UserService userService;
    private final CandidateRepository candidateRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("elections", electionService.getAllElections());
        model.addAttribute("users", userService.findAllUsers());
        return "admin/dashboard";
    }

    @GetMapping("/elections/new")
    public String newElectionForm(Model model) {
        model.addAttribute("election", new ElectionDto());
        return "admin/election-form";
    }

    @PostMapping("/elections")
    public String createElection(@Valid @ModelAttribute("election") ElectionDto electionDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/election-form";
        }

        try {
            electionService.createElection(electionDto);
            redirectAttributes.addFlashAttribute("successMessage", "Election created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create election: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/elections/{id}/candidates/add")
    public String addCandidateForm(@PathVariable Long id, Model model) {
        Election election = electionService.getElectionById(id);
        model.addAttribute("election", election);
        model.addAttribute("candidate", new CandidateDto());
        return "admin/candidate-form";
    }

    @PostMapping("/elections/{id}/candidates")
    public String addCandidate(@PathVariable Long id,
            @Valid @ModelAttribute("candidate") CandidateDto candidateDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("election", electionService.getElectionById(id));
            return "admin/candidate-form";
        }

        try {
            Election election = electionService.getElectionById(id);
            Candidate candidate = Candidate.builder()
                    .name(candidateDto.getName())
                    .description(candidateDto.getDescription())
                    .party(candidateDto.getParty())
                    .photoUrl(candidateDto.getPhotoUrl())
                    .election(election)
                    .build();
            candidateRepository.save(candidate);
            redirectAttributes.addFlashAttribute("successMessage", "Candidate added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add candidate: " + e.getMessage());
        }

        return "redirect:/admin/elections/" + id;
    }

    @GetMapping("/elections/{id}")
    public String viewElectionAdmin(@PathVariable Long id, Model model) {
        Election election = electionService.getElectionById(id);
        List<Candidate> candidates = candidateRepository.findByElectionId(id);
        model.addAttribute("election", election);
        model.addAttribute("candidates", candidates);
        model.addAttribute("results", electionService.getElectionResults(id));
        return "admin/election-detail";
    }

    @PostMapping("/elections/{id}/delete")
    public String deleteElection(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            electionService.deleteElection(id);
            redirectAttributes.addFlashAttribute("successMessage", "Election deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete election: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
