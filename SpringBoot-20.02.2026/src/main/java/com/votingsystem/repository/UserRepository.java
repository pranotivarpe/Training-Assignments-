package com.votingsystem.repository;

import com.votingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByVoterId(String voterId);

    boolean existsByEmail(String email);

    boolean existsByVoterId(String voterId);
}
