package org.unilab.uniplan.lector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.unilab.uniplan.lector.dto.LectorDto;
import org.unilab.uniplan.lector.dto.LectorRequestDto;
import org.unilab.uniplan.lector.dto.LectorResponseDto;
import java.util.UUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LectorController.class)
class LectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final UUID SAMPLE_ID = UUID.randomUUID();

    private LectorRequestDto requestDto;
    private LectorDto lectorDto;
    private LectorResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new LectorRequestDto(SAMPLE_ID,"g@gmail.com","Georgi","Likov");
        lectorDto = lectorMapper.toInternalDto(requestDto);
        responseDto = lectorMapper.toResponseDto(lectorDto);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LectorService lectorService() {
            return mock(LectorService.class);
        }
        @Bean
        public LectorMapper lectorMapper() {
            return mock(LectorMapper.class);
        }
    }

    @Autowired
    private LectorService lectorService;

    @Autowired
    private LectorMapper lectorMapper;

    @Test
    void testCreateLector() throws Exception {
        when(lectorMapper.toInternalDto(requestDto)).thenReturn(lectorDto);
        when(lectorService.createLector(lectorDto)).thenReturn(lectorDto);
        when(lectorMapper.toResponseDto(lectorDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/lectors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.firstName").value(responseDto.firstName()));
    }

}