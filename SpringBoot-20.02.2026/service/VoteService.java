package com.votingsystem.service;

import com.votingsystem.model.Vote;
import com.votingsystem.model.User;

public interface VoteService {

    Vote castVote(User voter, Long electionId, Long candidateId);

    boolean hasVoted(Long voterId, Long electionId);
}
