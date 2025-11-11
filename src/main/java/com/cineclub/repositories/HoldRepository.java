package com.cineclub.repositories;

import com.cineclub.entities.Hold;
import com.cineclub.entities.HoldStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoldRepository extends JpaRepository<Hold, Long> {
    
    List<Hold> findByStatus(HoldStatus status);
}
