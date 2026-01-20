package com.ekufrin.vhsrental.vhs;

import com.ekufrin.vhsrental.config.ApiResponse;
import com.ekufrin.vhsrental.config.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/vhs")
@RequiredArgsConstructor
public class VHSController {
    private final VHSService vhsService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VHSDTO>> createVHS(@Valid @ModelAttribute VhsCreateRequest request, @RequestParam(value = "image", required = false) MultipartFile image) {
        VHSDTO createdVHS = vhsService.createVHS(request, image);
        return ApiResponseFactory.success("VHS created successfully", createdVHS, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<VHSDTO>>> getAllVHS(Pageable pageable) {
        Page<VHSDTO> vhsList = vhsService.getAllVHS(pageable);
        return ApiResponseFactory.success("VHS list retrieved successfully", vhsList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VHSDTO>> getVHSById(@PathVariable UUID id) {
        VHSDTO vhs = vhsService.getVHSById(id);
        return ApiResponseFactory.success("VHS retrieved successfully", vhs, HttpStatus.OK);
    }
}
