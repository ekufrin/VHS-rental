package com.ekufrin.vhsrental.genre;

import com.ekufrin.vhsrental.exception.ResourceAlreadyExistsException;
import com.ekufrin.vhsrental.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public GenreDTO createGenre(GenreCreateRequest request) {
        if (genreRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Genre", "name", request.name());
        }
        Genre genre = Genre.builder()
                .name(request.name())
                .build();

        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toDTO(savedGenre);
    }

    @Transactional(readOnly = true)
    public Page<GenreDTO> getAllGenres(Pageable pageable) {
        return genreMapper.toDTO(genreRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public GenreDTO getGenreById(UUID id) {
        return genreMapper.toDTO(genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", id)));
    }
}
