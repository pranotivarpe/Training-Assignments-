package com.votingsystem.controller;

import com.votingsystem.model.Election;
import com.votingsystem.model.User;
import com.votingsystem.service.ElectionService;
import com.votingsystem.service.UserService;
import com.votingsystem.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ElectionService electionService;
    private final UserService userService;
    private final VoteService voteService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update election statuses based on current time
        electionService.updateElectionStatuses();

        List<Election> activeElections = electionService.getActiveElections();
        List<Election> upcomingElections = electionService.getUpcomingElections();
        List<Election> completedElections = electionService.getCompletedElections();

        model.addAttribute("user", user);
        model.addAttribute("activeElections", activeElections);
        model.addAttribute("upcomingElections", upcomingElections);
        model.addAttribute("completedElections", completedElections);

        // Check which elections the user has already voted in
        for (Election election : activeElections) {
            model.addAttribute("hasVoted_" + election.getId(),
                    voteService.hasVoted(user.getId(), election.getId()));
        }

        return "dashboard";
    }
}
