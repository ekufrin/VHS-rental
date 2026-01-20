package com.ekufrin.vhsrental.user;

import com.ekufrin.vhsrental.config.ApiResponse;
import com.ekufrin.vhsrental.config.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        UserDTO user = userService.getCurrentUser(userEmail);
        return ApiResponseFactory.success("Current user retrieved successfully", user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ApiResponseFactory.success("Users retrieved successfully", users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable UUID id) {
        UserDTO user = userService.getUserById(id);
        return ApiResponseFactory.success("User retrieved successfully", user, HttpStatus.OK);
    }

    @PatchMapping("/me/favorite-genres")
    public ResponseEntity<ApiResponse<UserDTO>> addFavoriteGenres(@RequestBody @Valid UserFavoriteGenresRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        UserDTO updatedUser = userService.addFavoriteGenres(request, userEmail);
        return ApiResponseFactory.success("Favorite genres updated successfully", updatedUser, HttpStatus.OK);
    }
}
