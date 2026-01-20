package com.ekufrin.vhsrental.rental;

import com.ekufrin.vhsrental.genre.GenreDTO;
import com.ekufrin.vhsrental.security.AuthService;
import com.ekufrin.vhsrental.security.CustomUserDetailsService;
import com.ekufrin.vhsrental.security.JWTUtil;
import com.ekufrin.vhsrental.status.Status;
import com.ekufrin.vhsrental.user.UserDTO;
import com.ekufrin.vhsrental.vhs.VHSDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RentalController.class)
@AutoConfigureMockMvc(addFilters = false)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private RentalService rentalService;

    private VHSDTO sampleVhs() {
        return VHSDTO.builder()
                .id(UUID.randomUUID())
                .title("Sample VHS")
                .releaseDate(Instant.parse("2020-01-01T00:00:00Z"))
                .genre(new GenreDTO(UUID.randomUUID(), "Action"))
                .rentalPrice(3.3)
                .stockLevel(2)
                .status(Status.AVAILABLE)
                .build();
    }

    private RentalDTO sampleRental() {
        return new RentalDTO(
                UUID.randomUUID().toString(),
                sampleVhs(),
                new UserDTO(UUID.randomUUID(), "User", "user@example.com", null),
                Instant.parse("2025-01-01T10:00:00Z"),
                Instant.parse("2025-01-04T10:00:00Z"),
                null,
                null
        );
    }

    @Test
    @DisplayName("create rental returns 200 OK")
    @WithMockUser(username = "user@example.com")
    void createRental_ReturnsOk() throws Exception {
        RentalDTO rentalDTO = sampleRental();
        given(rentalService.createRental(ArgumentMatchers.any(), ArgumentMatchers.anyString())).willReturn(rentalDTO);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vhsId\": \"" + rentalDTO.vhs().getId() + "\", \"dueDate\": \"2025-01-04T10:00:00Z\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.vhs.title").value("Sample VHS"));
    }

    @Test
    @DisplayName("finish rental returns 200 OK and sets return date")
    @WithMockUser(username = "user@example.com")
    void finishRental_ReturnsOk() throws Exception {
        RentalDTO finished = new RentalDTO(
                sampleRental().id(),
                sampleVhs(),
                new UserDTO(UUID.randomUUID(), "User", "user@example.com", null),
                Instant.parse("2025-01-01T10:00:00Z"),
                Instant.parse("2025-01-04T10:00:00Z"),
                Instant.parse("2025-01-03T12:00:00Z"),
                9.9
        );
        given(rentalService.finishRental(ArgumentMatchers.any(), ArgumentMatchers.anyString())).willReturn(finished);

        mockMvc.perform(patch("/rentals/" + finished.id() + "/finish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.returnDate").isNotEmpty())
                .andExpect(jsonPath("$.data.price").value(9.9));
    }

    @Test
    @DisplayName("get all rentals returns page")
    @WithMockUser(username = "user@example.com")
    void getAllRentals_ReturnsPage() throws Exception {
        Page<RentalDTO> page = new PageImpl<>(List.of(sampleRental()), PageRequest.of(0, 20), 1);
        given(rentalService.getAllRentals(ArgumentMatchers.any())).willReturn(page);

        mockMvc.perform(get("/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].vhs.title").value("Sample VHS"));
    }

    @Test
    @DisplayName("get rental by id returns rental")
    @WithMockUser(username = "user@example.com")
    void getRentalById_ReturnsRental() throws Exception {
        RentalDTO rental = sampleRental();
        given(rentalService.getRentalById(UUID.fromString(rental.id()))).willReturn(rental);

        mockMvc.perform(get("/rentals/" + rental.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.email").value("user@example.com"));
    }
}
