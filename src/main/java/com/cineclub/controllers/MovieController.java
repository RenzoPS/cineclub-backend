package com.cineclub.controllers;

import com.cineclub.dtos.MovieDto;
import com.cineclub.dtos.MovieFilterDto;
import com.cineclub.dtos.MovieResponseDto;
import com.cineclub.entities.Movie;
import com.cineclub.mappers.MovieMapper;
import com.cineclub.services.MovieService;
import com.cineclub.specifications.MovieSpecification;
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
@RequestMapping("/api/movies")
public class MovieController {
    
    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @PostMapping
    public ResponseEntity<MovieDto> create(@Valid @RequestBody MovieDto movieDto) {
        Movie movie = movieMapper.toEntity(movieDto);
        movieService.save(movie);
        return ResponseEntity.ok(movieMapper.toDto(movie));
    }

    @GetMapping
    public ResponseEntity<Page<MovieResponseDto>> getAll(
            @PageableDefault(page = 0, size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Movie> moviePage = movieService.findAll(pageable);
        Page<MovieResponseDto> result = moviePage.map(movieMapper::toResponseDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDto> getById(@PathVariable Long id) {
        Movie movie = movieService.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return ResponseEntity.ok(movieMapper.toResponseDto(movie));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<MovieResponseDto>> search(
            @RequestBody MovieFilterDto filter,
            @PageableDefault(page = 0, size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Specification<Movie> spec = MovieSpecification.buildFilter(filter);
        Page<Movie> moviePage = movieService.findAllWithSpecification(spec, pageable);
        Page<MovieResponseDto> result = moviePage.map(movieMapper::toResponseDto);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> update(@PathVariable Long id, @Valid @RequestBody MovieDto movieDto){
        Movie updates = movieMapper.toEntity(movieDto);
        Movie updated = movieService.update(id, updates);
        return ResponseEntity.ok(movieMapper.toDto(updated));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MovieDto> partialUpdate(@PathVariable Long id, @RequestBody MovieDto movieDto){
        Movie updates = movieMapper.toEntity(movieDto);
        Movie updated = movieService.partialUpdate(id, updates);
        return ResponseEntity.ok(movieMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}