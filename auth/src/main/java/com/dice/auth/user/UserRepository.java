package com.dice.auth.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("select u from UserEntity u WHERE u.createdAt < :before AND NOT u.registered")
    List<UserEntity> getAllNotRegisteredUsersOlderThan(Instant before);
}