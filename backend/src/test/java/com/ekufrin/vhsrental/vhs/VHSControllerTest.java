package com.ekufrin.vhsrental.vhs;

import com.ekufrin.vhsrental.genre.GenreDTO;
import com.ekufrin.vhsrental.security.AuthService;
import com.ekufrin.vhsrental.security.CustomUserDetailsService;
import com.ekufrin.vhsrental.security.JWTUtil;
import com.ekufrin.vhsrental.status.Status;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VHSController.class)
@AutoConfigureMockMvc(addFilters = false)
class VHSControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VHSService vhsService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("get all VHS returns paged list")
    void getAllVHS_ReturnsPage() throws Exception {
        GenreDTO genre = new GenreDTO(UUID.randomUUID(), "Action");
        VHSDTO dto = VHSDTO.builder()
                .id(UUID.randomUUID())
                .title("Test Tape")
                .releaseDate(Instant.parse("2010-01-01T00:00:00Z"))
                .genre(genre)
                .rentalPrice(4.0)
                .stockLevel(3)
                .status(Status.AVAILABLE)
                .build();
        Page<VHSDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 20), 1);
        given(vhsService.getAllVHS(ArgumentMatchers.any())).willReturn(page);

        mockMvc.perform(get("/vhs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("Test Tape"));
    }

    @Test
    @DisplayName("get VHS by id returns VHS data")
    void getVHSById_ReturnsVHS() throws Exception {
        GenreDTO genre = new GenreDTO(UUID.randomUUID(), "Drama");
        VHSDTO dto = VHSDTO.builder()
                .id(UUID.fromString("33333333-cccc-4c3c-a000-000000000001"))
                .title("Drama Tape")
                .releaseDate(Instant.parse("2012-05-05T00:00:00Z"))
                .genre(genre)
                .rentalPrice(3.3)
                .stockLevel(2)
                .status(Status.AVAILABLE)
                .build();
        given(vhsService.getVHSById(dto.getId())).willReturn(dto);

        mockMvc.perform(get("/vhs/33333333-cccc-4c3c-a000-000000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Drama Tape"))
                .andExpect(jsonPath("$.data.genre.name").value("Drama"));
    }

    @Test
    @DisplayName("create VHS with multipart request returns 201 Created")
    void createVHS_Multipart_ReturnsCreated() throws Exception {
        GenreDTO genre = new GenreDTO(UUID.randomUUID(), "Comedy");
        VHSDTO dto = VHSDTO.builder()
                .id(UUID.randomUUID())
                .title("Funny Tape")
                .releaseDate(Instant.parse("2015-03-03T00:00:00Z"))
                .genre(genre)
                .rentalPrice(5.5)
                .stockLevel(4)
                .status(Status.AVAILABLE)
                .build();
        given(vhsService.createVHS(ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(dto);

        MockMultipartFile image = new MockMultipartFile("image", "cover.jpg", "image/jpeg", "img".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/vhs")
                        .file(image)
                        .param("title", "Funny Tape")
                        .param("releaseDate", "2015-03-03T00:00:00Z")
                        .param("genreId", UUID.randomUUID().toString())
                        .param("rentalPrice", "5.5")
                        .param("stockLevel", "4")
                        .param("status", "AVAILABLE")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Funny Tape"));
    }
}
