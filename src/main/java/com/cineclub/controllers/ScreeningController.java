package com.cineclub.controllers;

import com.cineclub.dtos.*;
import com.cineclub.entities.Screening;
import com.cineclub.entities.Seat;
import com.cineclub.entities.Ticket;
import com.cineclub.entities.User;
import com.cineclub.mappers.ScreeningMapper;
import com.cineclub.mappers.SeatMapper;
import com.cineclub.mappers.TicketMapper;
import com.cineclub.services.HoldService;
import com.cineclub.services.MovieService;
import com.cineclub.services.RoomService;
import com.cineclub.services.ScreeningService;
import com.cineclub.services.SeatService;
import com.cineclub.specifications.ScreeningSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/screenings")
public class ScreeningController {
    
    private final ScreeningService screeningService;
    private final ScreeningMapper screeningMapper;
    private final MovieService movieService;
    private final RoomService roomService;
    private final HoldService holdService;
    private final TicketMapper ticketMapper;
    private final SeatService seatService;
    private final SeatMapper seatMapper;

    @PostMapping
    public ResponseEntity<ScreeningDto> create(@Valid @RequestBody ScreeningDto screeningDto) {
        Screening screening = screeningMapper.toEntity(screeningDto);
        
        // Setear las relaciones
        screening.setMovie(movieService.findById(screeningDto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found")));
        screening.setRoom(roomService.findById(screeningDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found")));
        
        screeningService.save(screening);
        return ResponseEntity.ok(screeningMapper.toDto(screening));
    }

    @GetMapping
    public ResponseEntity<Page<ScreeningResponseDto>> getAll(
            @PageableDefault(page = 0, size = 10, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Screening> screeningPage = screeningService.findAll(pageable);
        Page<ScreeningResponseDto> result = screeningPage.map(screeningMapper::toResponseDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreeningResponseDto> getById(@PathVariable Long id) {
        Screening screening = screeningService.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
        return ResponseEntity.ok(screeningMapper.toResponseDto(screening));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ScreeningResponseDto>> search(
            @RequestBody ScreeningFilterDto filter,
            @PageableDefault(page = 0, size = 10, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Specification<Screening> spec = ScreeningSpecification.buildFilter(filter);
        Page<Screening> screeningPage = screeningService.findWithSpecification(spec, pageable);
        Page<ScreeningResponseDto> result = screeningPage.map(screeningMapper::toResponseDto);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ScreeningDto> update(@PathVariable Long id, @RequestBody ScreeningDto screeningDto) {
        Screening updated = screeningService.update(
            id,
            screeningDto.getMovieId(),
            screeningDto.getRoomId(),
            screeningDto.getStartTime()
        );
        return ResponseEntity.ok(screeningMapper.toDto(updated));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ScreeningDto> partialUpdate(@PathVariable Long id, @RequestBody ScreeningDto screeningDto) {
        Screening updated = screeningService.partialUpdate(
            id,
            screeningDto.getMovieId(),
            screeningDto.getRoomId(),
            screeningDto.getStartTime()
        );
        return ResponseEntity.ok(screeningMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        screeningService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/holds")
    public ResponseEntity<List<TicketDto>> createHold(
            @PathVariable Long id,
            @Valid @RequestBody HoldDto holdDto,
            @AuthenticationPrincipal User user) {
        
        List<Ticket> tickets = holdService.createHold(user.getId(), id, holdDto.getSeats());
        return ResponseEntity.ok(ticketMapper.toDtoList(tickets));
    }
    
    @GetMapping("/{id}/seats")
    public ResponseEntity<Page<SeatDto>> getSeats(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        
        Screening screening = screeningService.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));
        
        Page<Seat> seatPage = seatService.getSeatsForScreening(screening, pageable);
        return ResponseEntity.ok(seatPage.map(seatMapper::toDto));
    }
}
