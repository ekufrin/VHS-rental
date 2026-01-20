package com.ekufrin.vhsrental.genre;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreDTO toDTO(Genre genre);

    default Page<GenreDTO> toDTO(Page<Genre> genres) {
        return genres.map(this::toDTO);
    }
}
