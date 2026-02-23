package com.votingsystem.repository;

import com.votingsystem.model.Election;
import com.votingsystem.model.ElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    List<Election> findByStatus(ElectionStatus status);

    List<Election> findByStatusIn(List<ElectionStatus> statuses);

    List<Election> findAllByOrderByStartDateDesc();

    @Query("SELECT DISTINCT e FROM Election e LEFT JOIN FETCH e.candidates ORDER BY e.startDate DESC")
    List<Election> findAllWithCandidatesOrderByStartDateDesc();

    @Query("SELECT e FROM Election e LEFT JOIN FETCH e.candidates WHERE e.id = :id")
    Optional<Election> findByIdWithCandidates(Long id);
}
