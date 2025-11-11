package com.cineclub.services;

import com.cineclub.entities.Room;
import com.cineclub.entities.Seat;
import com.cineclub.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final SeatService seatService;
    private final jakarta.persistence.EntityManager entityManager;

    @Transactional
    public Room save(Room room){
        // Primero guardar la sala para obtener el ID
        room = roomRepository.save(room);
        
        // Luego generar y guardar los asientos
        List<Seat> seats = seatService.generateSeatsForRoom(room);
        room.setSeats(seats);
        
        return room;
    }

    public Page<Room> findAll(Pageable pageable){
        return roomRepository.findAll(pageable);
    }

    public Optional<Room> findById(Long id){
        return roomRepository.findById(id);
    }

    public Optional<Room> findByNumber(int number) {
        return roomRepository.findByNumber(number);
    }

    @Transactional
    public Room update(Long id, Room updates) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setNumber(updates.getNumber());

        if(room.getCapacity() != updates.getCapacity()){
            room.setCapacity(updates.getCapacity());
            
            room.getSeats().clear();  // Eliminar asientos viejos
            entityManager.flush();  // Forzar que se ejecuten los DELETE en la BD antes de insertar nuevos
            
            // Crear y agregar nuevos asientos
            List<Seat> newSeats = seatService.generateSeatsForRoom(room);
            room.getSeats().addAll(newSeats);
        }

        return room;
    }

    @Transactional
    public Room partialUpdate(Long id, Integer number, Integer capacity) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (number != null) {
            room.setNumber(number);
        }

        if (capacity != null && !capacity.equals(room.getCapacity())) {
            room.setCapacity(capacity);
            
            room.getSeats().clear();  // Eliminar asientos viejos
            entityManager.flush();  // Forzar que se ejecuten los DELETE en la BD antes de insertar nuevos

            // Crear y agregar nuevos asientos
            List<Seat> newSeats = seatService.generateSeatsForRoom(room);
            room.getSeats().addAll(newSeats);
        }
        
        return room;
    }

    @Transactional
    public void delete(Long id){
        roomRepository.deleteById(id);
    }
}
