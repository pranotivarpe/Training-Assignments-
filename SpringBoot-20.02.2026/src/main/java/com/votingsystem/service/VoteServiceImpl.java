package com.votingsystem.service;

import com.votingsystem.exception.ResourceNotFoundException;
import com.votingsystem.exception.VotingException;
import com.votingsystem.model.*;
import com.votingsystem.repository.CandidateRepository;
import com.votingsystem.repository.ElectionRepository;
import com.votingsystem.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;

    @Override
    public Vote castVote(User voter, Long electionId, Long candidateId) {
        // 1. Check if election exists
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", electionId));

        // 2. Check if election is active
        if (election.computeStatus() != ElectionStatus.ACTIVE) {
            throw new VotingException("This election is not currently active. Voting is not allowed.");
        }

        // 3. Check if voter has already voted in this election
        if (voteRepository.existsByVoterIdAndElectionId(voter.getId(), electionId)) {
            throw new VotingException("You have already voted in this election.");
        }

        // 4. Check if candidate belongs to this election
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", candidateId));

        if (!candidate.getElection().getId().equals(electionId)) {
            throw new VotingException("This candidate does not belong to the selected election.");
        }

        // 5. Cast the vote
        Vote vote = Vote.builder()
                .voter(voter)
                .election(election)
                .candidate(candidate)
                .build();

        return voteRepository.save(vote);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasVoted(Long voterId, Long electionId) {
        return voteRepository.existsByVoterIdAndElectionId(voterId, electionId);
    }
}
