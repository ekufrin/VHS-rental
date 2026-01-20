package com.ekufrin.vhsrental.review;

import com.ekufrin.vhsrental.user.User;
import com.ekufrin.vhsrental.vhs.VHS;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "user", expression = "java(toUserSummary(review.getRental().getUser()))")
    @Mapping(target = "vhs", expression = "java(toVhsSummary(review.getRental().getVhs()))")
    ReviewDTO toDTO(Review review);

    default Page<ReviewDTO> toDTO(Page<Review> reviews) {
        return reviews.map(this::toDTO);
    }

    default UserSummaryDTO toUserSummary(User user) {
        return user == null ? null : new UserSummaryDTO(user.getEmail());
    }

    default VHSSummaryDTO toVhsSummary(VHS vhs) {
        return vhs == null ? null : new VHSSummaryDTO(vhs.getId(), vhs.getTitle(), vhs.getGenre().getName(), vhs.getReleaseDate());
    }
}
