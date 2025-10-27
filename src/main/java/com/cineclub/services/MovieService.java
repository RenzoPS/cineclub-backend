package com.cineclub.services;

import com.cineclub.entities.Movie;
import com.cineclub.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    @Transactional
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public Page<Movie> findAll(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public Page<Movie> findAllWithSpecification(Specification<Movie> spec, Pageable pageable) {
        return movieRepository.findAll(spec, pageable);
    }

    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    @Transactional
    public Movie update(Long id, Movie updates){
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        movie.setTitle(updates.getTitle());
        movie.setDescription(updates.getDescription());
        movie.setDuration(updates.getDuration());
        movie.setGenre(updates.getGenre());
        movie.setRating(updates.getRating());

        return movie;
    }

    @Transactional
    public Movie partialUpdate(Long id, Movie updates){
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        if(updates.getTitle() != null){
            movie.setTitle(updates.getTitle());
        }
        if(updates.getDescription() != null){
            movie.setDescription(updates.getDescription());
        }
        if(updates.getDuration() != null){
            movie.setDuration(updates.getDuration());
        }
        if(updates.getGenre() != null){
            movie.setGenre(updates.getGenre());
        }
        if(updates.getRating() != null){
            movie.setRating(updates.getRating());
        }

        return movie;
    }

    @Transactional
    public void delete(Long id) {
        movieRepository.deleteById(id);
    }
}
