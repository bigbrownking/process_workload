package org.example.proccessworkload_microservice.repository;

import org.example.proccessworkload_microservice.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, String> {
    Optional<Trainer> findByUsername(String username);
}
