package com.ekufrin.vhsrental.user;

import com.ekufrin.vhsrental.exception.ResourceNotFoundException;
import com.ekufrin.vhsrental.genre.Genre;
import com.ekufrin.vhsrental.genre.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userMapper.toDTO(userRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toDTO(user);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDTO addFavoriteGenres(UserFavoriteGenresRequest request, String userEmail) {
        List<UUID> requestGenreIds = request.favoriteGenres();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
        List<Genre> genres = genreRepository.findAllByIdIn(requestGenreIds);
        user.setFavoriteGenres(new ArrayList<>(genres));
        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }
}
