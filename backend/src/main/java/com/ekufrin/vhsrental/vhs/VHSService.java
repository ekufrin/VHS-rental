package com.ekufrin.vhsrental.vhs;

import com.ekufrin.vhsrental.exception.InvalidOperationException;
import com.ekufrin.vhsrental.exception.ResourceNotFoundException;
import com.ekufrin.vhsrental.genre.Genre;
import com.ekufrin.vhsrental.genre.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VHSService {
    private static final long MAX_FILE_SIZE = 10_000_000; // 10MB
    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png");
    private final VHSRepository vhsRepository;
    private final GenreRepository genreRepository;
    private final VHSMapper vhsMapper;
    private final Path uploadPath = Paths.get("src/main/resources/static/uploads");

    @Transactional(readOnly = true)
    public Page<VHSDTO> getAllVHS(Pageable pageable) {
        return vhsMapper.toDTO(vhsRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public VHSDTO getVHSById(UUID id) {
        VHS vhs = vhsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VHS", "id", id));
        return vhsMapper.toDTO(vhs);
    }

    public VHSDTO createVHS(VhsCreateRequest request, MultipartFile image) {
        Genre genre = genreRepository.findById(UUID.fromString(request.genreId()))
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "id", request.genreId()));

        UUID imageId = null;
        String imageExtension = null;
        if (image != null && !image.isEmpty()) {
            imageId = UUID.randomUUID();
            imageExtension = saveImage(image, imageId);
        }

        VHS vhs = VHS.builder()
                .title(request.title())
                .releaseDate(Instant.parse(request.releaseDate()))
                .genre(genre)
                .rentalPrice(request.rentalPrice())
                .stockLevel(request.stockLevel())
                .imageId(imageId)
                .imageExtension(imageExtension)
                .status(request.status())
                .build();

        VHS savedVHS = vhsRepository.save(vhs);
        return vhsMapper.toDTO(savedVHS);
    }

    private String saveImage(MultipartFile image, UUID imageId) {
        try {
            if (image.getSize() > MAX_FILE_SIZE) {
                throw new InvalidOperationException(
                        "File too large. Maximum size is 10MB"
                );
            }

            String contentType = image.getContentType();
            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
                throw new InvalidOperationException(
                        "Invalid file type. Only JPEG and PNG are allowed"
                );
            }

            String originalFilename = image.getOriginalFilename();
            if (originalFilename == null ||
                    !originalFilename.matches(".*\\.(jpg|jpeg|png)$")) {
                throw new InvalidOperationException(
                        "Invalid file extension. Only .jpg, .jpeg, and .png are allowed"
                );
            }

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String extension = contentType.equals("image/jpeg") ? ".jpg" : ".png";
            String filename = imageId + extension;
            Path filePath = uploadPath.resolve(filename);

            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return extension;
        } catch (IOException e) {
            throw new InvalidOperationException(
                    "Failed to save image: " + e.getMessage(), e
            );
        }
    }

}
