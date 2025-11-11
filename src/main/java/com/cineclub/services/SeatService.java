package com.cineclub.services;

import com.cineclub.entities.Room;
import com.cineclub.entities.Screening;
import com.cineclub.entities.Seat;
import com.cineclub.repositories.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Pageable.unpaged;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    public Page<Seat> findByRoomId(Long roomId, Pageable pageable) {
        return seatRepository.findByRoomId(roomId, pageable);
    }

    public Page<Seat> findByRoomIdAndRowLetter(Long roomId, String rowLetter, Pageable pageable) {
        return seatRepository.findByRoomIdAndRowLetter(roomId, rowLetter, pageable);
    }
    
    public Page<Seat> getSeatsForScreening(Screening screening, Pageable pageable) {
        return seatRepository.findByRoomId(screening.getRoom().getId(), pageable);
    }

    @Transactional
    public List<Seat> generateSeatsForRoom(Room room){
        int capacity = room.getCapacity();
        int seatsPerRow = 10;
        List<Seat> seats = new ArrayList<>();

        for(int i = 0; i < capacity; i++){
            Seat seat = new Seat();
            seat.setRoom(room);
            seat.setRowLetter(getRowLetter(i / seatsPerRow));
            seat.setSeatNumber((i % seatsPerRow) + 1);
            seat.setIsAvailable(true);
            seats.add(seat);
        }
        
        // Guardar todos los asientos en la BD
        return seatRepository.saveAll(seats);
    }

    private String getRowLetter(int index) {
        StringBuilder result = new StringBuilder();
        while (index >= 0) {
            result.insert(0, (char)('A' + (index % 26)));
            index = (index / 26) - 1;
        }
        return result.toString();
    }

}
