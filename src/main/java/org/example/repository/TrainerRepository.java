package org.example.repository;

import org.example.model.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends MongoRepository<Trainer, String> {
    Optional<Trainer> findByUsername(String username);
}
