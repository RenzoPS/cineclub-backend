package com.cineclub.controllers;

import com.cineclub.dtos.ScreeningDto;
import com.cineclub.dtos.ScreeningResponseDto;
import com.cineclub.dtos.ScreeningFilterDto;
import com.cineclub.entities.Screening;
import com.cineclub.mappers.ScreeningMapper;
import com.cineclub.services.MovieService;
import com.cineclub.services.RoomService;
import com.cineclub.services.ScreeningService;
import com.cineclub.specifications.ScreeningSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/screenings")
public class ScreeningController {
    
    private final ScreeningService screeningService;
    private final ScreeningMapper screeningMapper;
    private final MovieService movieService;
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<ScreeningDto> create(@Valid @RequestBody ScreeningDto screeningDto) {
        Screening screening = screeningMapper.toEntity(screeningDto);
        
        // Setear las relaciones
        screening.setMovie(movieService.findById(screeningDto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found")));
        screening.setRoom(roomService.findById(screeningDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found")));

        // Calcular endTime
        screening.setEndTime(screening.getStartTime().plusMinutes(screening.getMovie().getDuration()));
        
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

}
