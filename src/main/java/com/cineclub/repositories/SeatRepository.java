package com.cineclub.repositories;

import com.cineclub.entities.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    Page<Seat> findByRoomId(Long roomId, Pageable pageable);
    
    Page<Seat> findByRoomIdAndRowLetter(Long roomId, String rowLetter, Pageable pageable);
    
    java.util.Optional<Seat> findByRoomIdAndRowLetterAndSeatNumber(Long roomId, String rowLetter, Integer seatNumber);
}
