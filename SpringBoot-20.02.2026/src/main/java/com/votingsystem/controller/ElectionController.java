package com.votingsystem.controller;

import com.votingsystem.model.Candidate;
import com.votingsystem.model.Election;
import com.votingsystem.model.User;
import com.votingsystem.service.ElectionService;
import com.votingsystem.service.UserService;
import com.votingsystem.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;
    private final UserService userService;
    private final VoteService voteService;

    @GetMapping("/{id}")
    public String viewElection(@PathVariable Long id, Authentication authentication, Model model) {
        Election election = electionService.getElectionById(id);
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Candidate> candidates = election.getCandidates();
        boolean hasVoted = voteService.hasVoted(user.getId(), id);

        model.addAttribute("election", election);
        model.addAttribute("candidates", candidates);
        model.addAttribute("hasVoted", hasVoted);
        model.addAttribute("user", user);

        return "election-detail";
    }

    @PostMapping("/{id}/vote")
    public String castVote(@PathVariable Long id,
            @RequestParam Long candidateId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            voteService.castVote(user, id, candidateId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your vote has been cast successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/elections/" + id;
    }

    @GetMapping("/{id}/results")
    public String viewResults(@PathVariable Long id, Model model) {
        Map<String, Object> results = electionService.getElectionResults(id);
        model.addAttribute("election", results.get("election"));
        model.addAttribute("candidates", results.get("candidates"));
        model.addAttribute("totalVotes", results.get("totalVotes"));
        model.addAttribute("winners", results.get("winners"));
        return "election-results";
    }
}
