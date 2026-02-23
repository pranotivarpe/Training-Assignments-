package com.votingsystem.service;

import com.votingsystem.dto.CandidateDto;
import com.votingsystem.dto.ElectionDto;
import com.votingsystem.exception.ResourceNotFoundException;
import com.votingsystem.model.Candidate;
import com.votingsystem.model.Election;
import com.votingsystem.model.ElectionStatus;
import com.votingsystem.repository.CandidateRepository;
import com.votingsystem.repository.ElectionRepository;
import com.votingsystem.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;

    @Override
    public Election createElection(ElectionDto dto) {
        Election election = Election.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(ElectionStatus.UPCOMING)
                .build();

        Election savedElection = electionRepository.save(election);

        // Add candidates if provided
        if (dto.getCandidates() != null && !dto.getCandidates().isEmpty()) {
            for (CandidateDto candidateDto : dto.getCandidates()) {
                Candidate candidate = Candidate.builder()
                        .name(candidateDto.getName())
                        .description(candidateDto.getDescription())
                        .party(candidateDto.getParty())
                        .photoUrl(candidateDto.getPhotoUrl())
                        .election(savedElection)
                        .build();
                candidateRepository.save(candidate);
                savedElection.getCandidates().add(candidate);
            }
        }

        return savedElection;
    }

    @Override
    @Transactional(readOnly = true)
    public Election getElectionById(Long id) {
        return electionRepository.findByIdWithCandidates(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Election> getAllElections() {
        List<Election> elections = electionRepository.findAllWithCandidatesOrderByStartDateDesc();
        elections.forEach(e -> e.setStatus(e.computeStatus()));
        return elections;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Election> getActiveElections() {
        return getAllElections().stream()
                .filter(e -> e.computeStatus() == ElectionStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Election> getUpcomingElections() {
        return getAllElections().stream()
                .filter(e -> e.computeStatus() == ElectionStatus.UPCOMING)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Election> getCompletedElections() {
        return getAllElections().stream()
                .filter(e -> e.computeStatus() == ElectionStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    @Override
    public void updateElectionStatuses() {
        List<Election> elections = electionRepository.findAll();
        for (Election election : elections) {
            ElectionStatus computedStatus = election.computeStatus();
            if (election.getStatus() != computedStatus) {
                election.setStatus(computedStatus);
                electionRepository.save(election);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getElectionResults(Long electionId) {
        Election election = getElectionById(electionId);
        List<Candidate> candidates = candidateRepository.findByElectionId(electionId);
        List<Object[]> voteCounts = voteRepository.countVotesByCandidateForElection(electionId);
        long totalVotes = voteRepository.countByElectionId(electionId);

        // Build a map of candidateId -> voteCount
        Map<Long, Long> voteCountMap = new HashMap<>();
        for (Object[] row : voteCounts) {
            voteCountMap.put((Long) row[0], (Long) row[1]);
        }

        // Build result DTOs
        List<CandidateDto> results = candidates.stream().map(c -> {
            CandidateDto dto = new CandidateDto();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setParty(c.getParty());
            dto.setDescription(c.getDescription());
            dto.setVoteCount(voteCountMap.getOrDefault(c.getId(), 0L).intValue());
            return dto;
        }).sorted((a, b) -> b.getVoteCount() - a.getVoteCount())
                .collect(Collectors.toList());

        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("election", election);
        resultMap.put("candidates", results);
        resultMap.put("totalVotes", totalVotes);

        // Determine winner(s) if election is completed
        if (election.computeStatus() == ElectionStatus.COMPLETED && !results.isEmpty()) {
            int maxVotes = results.get(0).getVoteCount();
            List<CandidateDto> winners = results.stream()
                    .filter(c -> c.getVoteCount() == maxVotes)
                    .collect(Collectors.toList());
            resultMap.put("winners", winners);
        }

        return resultMap;
    }

    @Override
    public void deleteElection(Long id) {
        Election election = getElectionById(id);
        electionRepository.delete(election);
    }
}
