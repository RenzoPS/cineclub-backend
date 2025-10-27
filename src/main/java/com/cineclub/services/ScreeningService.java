package com.cineclub.services;

import com.cineclub.entities.Screening;
import com.cineclub.repositories.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final MovieService movieService;
    private final RoomService roomService;

    @Transactional
    public Screening save(Screening screening) {
        return screeningRepository.save(screening);
    }

    public Page<Screening> findAll(Pageable pageable){
        return screeningRepository.findAll(pageable);
    }

    public Page<Screening> findWithSpecification(Specification<Screening> spec, Pageable pageable){
        return screeningRepository.findAll(spec, pageable);
    }

    public Optional<Screening> findById(Long id) {
        return screeningRepository.findById(id);
    }

    @Transactional
    public Screening update(Long id, Long movieId, Long roomId, LocalDateTime startTime) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        screening.setMovie(movieService.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found")));
        screening.setRoom(roomService.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found")));

        screening.setStartTime(startTime);

        return screening;
    }

    @Transactional
    public Screening partialUpdate(Long id, Long movieId, Long roomId, LocalDateTime startTime) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        if (movieId != null) {
            screening.setMovie(movieService.findById(movieId)
                    .orElseThrow(() -> new RuntimeException("Movie not found")));
        }
        if (roomId != null) {
            screening.setRoom(roomService.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Room not found")));
        }
        if (startTime != null) {
            screening.setStartTime(startTime);
        }

        return screening;
    }

    @Transactional
    public void delete(Long id) {
        screeningRepository.deleteById(id);
    }
}
