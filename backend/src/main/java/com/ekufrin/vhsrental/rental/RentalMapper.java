package com.ekufrin.vhsrental.rental;

import com.ekufrin.vhsrental.vhs.VHS;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface RentalMapper {
    RentalDTO toDTO(Rental rental);

    default Page<RentalDTO> toDTO(Page<Rental> rentals) {
        return rentals.map(this::toDTO);
    }

    @AfterMapping
    default void setVhsImageUrl(@MappingTarget RentalDTO dto, Rental rental) {
        if (dto.vhs() != null && rental.getVhs() != null) {
            VHS vhs = rental.getVhs();
            if (vhs.getImageId() != null && vhs.getImageExtension() != null) {
                dto.vhs().setImageUrl("/uploads/" + vhs.getImageId() + vhs.getImageExtension());
            }
        }
    }
}
