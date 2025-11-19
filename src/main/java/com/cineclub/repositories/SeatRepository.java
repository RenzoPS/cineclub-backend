package com.cineclub.repositories;

import com.cineclub.entities.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    Page<Seat> findByRoomId(Long roomId, Pageable pageable);
    
    Page<Seat> findByRoomIdAndRowLetter(Long roomId, String rowLetter, Pageable pageable);
    
    Optional<Seat> findByRoomIdAndRowLetterAndSeatNumber(Long roomId, String rowLetter, Integer seatNumber);

    Page<Seat> findByRoomIdAndIsAvailable(Long roomId, Boolean isAvailable, Pageable pageable);
}
