package com.cineclub.repositories;

import com.cineclub.entities.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long>, JpaSpecificationExecutor<Screening> {
    List<Screening> findByRoomId(Long roomId);
}
