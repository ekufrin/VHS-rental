package com.ekufrin.vhsrental.vhs;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface VHSMapper {
    VHSDTO toDTO(VHS vhs);

    default Page<VHSDTO> toDTO(Page<VHS> vhsList) {
        return vhsList.map(this::toDTO);
    }

    @AfterMapping
    default void setImageUrl(@MappingTarget VHSDTO dto, VHS vhs) {
        if (vhs.getImageId() != null && vhs.getImageExtension() != null) {
            dto.setImageUrl("/uploads/" + vhs.getImageId() + vhs.getImageExtension());
        }
    }
}
