package com.cineclub.controllers;

import com.cineclub.dtos.RoomDto;
import com.cineclub.dtos.SeatDto;
import com.cineclub.entities.Room;
import com.cineclub.entities.Seat;
import com.cineclub.mappers.RoomMapper;
import com.cineclub.mappers.SeatMapper;
import com.cineclub.services.RoomService;
import com.cineclub.services.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {
    
    private final RoomService roomService;
    private final RoomMapper roomMapper;
    private final SeatService seatService;
    private final SeatMapper seatMapper;

    @PostMapping
    public ResponseEntity<RoomDto> create(@Valid @RequestBody RoomDto roomDto) {
        Room room = roomMapper.toEntity(roomDto);
        roomService.save(room);
        return ResponseEntity.ok(roomMapper.toDto(room));
    }

    @GetMapping
    public ResponseEntity<Page<RoomDto>> getAll(
            @PageableDefault(page = 0, size = 10, sort = "number", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Room> roomPage = roomService.findAll(pageable);
        Page<RoomDto> result = roomPage.map(roomMapper::toDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getById(@PathVariable Long id) {
        Room room = roomService.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return ResponseEntity.ok(roomMapper.toDto(room));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<Page<SeatDto>> getSeatsByRoomId(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 10, sort = {"roomId", "rowLetter", "seatNumber"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Seat> seatPage = seatService.findByRoomId(id, pageable);
        Page<SeatDto> result = seatPage.map(seatMapper::toDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/seats/row/{rowLetter}")
    public ResponseEntity<Page<SeatDto>> getByRoomAndRow(
            @PathVariable Long id,
            @PathVariable String rowLetter,
            @PageableDefault(page = 0, sort = {"rowLetter", "seatNumber"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Seat> seats = seatService.findByRoomIdAndRowLetter(id, rowLetter, pageable);
        Page<SeatDto> result = seats.map(seatMapper::toDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<RoomDto> getByNumber(@PathVariable Integer number) {
        Room room = roomService.findByNumber(number)
                .orElseThrow(() -> new RuntimeException("Room with number " + number + " not found"));
        return ResponseEntity.ok(roomMapper.toDto(room));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> update(@PathVariable Long id, @Valid @RequestBody RoomDto roomDto) {
        Room updates = roomMapper.toEntity(roomDto);
        Room updated = roomService.update(id, updates);
        return ResponseEntity.ok(roomMapper.toDto(updated));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RoomDto> partialUpdate(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        Room updated = roomService.partialUpdate(id, roomDto.getNumber(), roomDto.getCapacity());
        return ResponseEntity.ok(roomMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
