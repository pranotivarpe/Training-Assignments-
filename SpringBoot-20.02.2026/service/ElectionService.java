package com.votingsystem.service;

import com.votingsystem.dto.ElectionDto;
import com.votingsystem.model.Election;

import java.util.List;
import java.util.Map;

public interface ElectionService {

    Election createElection(ElectionDto electionDto);

    Election getElectionById(Long id);

    List<Election> getAllElections();

    List<Election> getActiveElections();

    List<Election> getUpcomingElections();

    List<Election> getCompletedElections();

    void updateElectionStatuses();

    Map<String, Object> getElectionResults(Long electionId);

    void deleteElection(Long id);
}
