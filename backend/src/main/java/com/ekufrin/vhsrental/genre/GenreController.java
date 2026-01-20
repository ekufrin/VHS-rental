package com.ekufrin.vhsrental.genre;

import com.ekufrin.vhsrental.config.ApiResponse;
import com.ekufrin.vhsrental.config.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<ApiResponse<GenreDTO>> createGenre(@RequestBody @Valid GenreCreateRequest request) {
        GenreDTO createdGenre = genreService.createGenre(request);
        return ApiResponseFactory.success("Genre created successfully", createdGenre, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GenreDTO>>> getAllGenres(Pageable pageable) {
        Page<GenreDTO> genres = genreService.getAllGenres(pageable);
        return ApiResponseFactory.success("Genres retrieved successfully", genres, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreDTO>> getGenreById(@PathVariable UUID id) {
        GenreDTO genre = genreService.getGenreById(id);
        return ApiResponseFactory.success("Genre retrieved successfully", genre, HttpStatus.OK);
    }
}
