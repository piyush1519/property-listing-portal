package com.propertyportal.backend.integration;

import com.propertyportal.backend.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PropertyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropertyRepository propertyRepository;

    @BeforeEach
    void setUp() {
        propertyRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveProperty() throws Exception {

        String propertyJson = """
                {
                    "title": "Integration Test Apartment",
                    "description": "Apartment created during integration testing",
                    "location": "Mumbai",
                    "type": "Apartment",
                    "price": 7500000,
                    "bedrooms": 2,
                    "area": 950,
                    "ownerAgent": "Integration Agent",
                    "status": "AVAILABLE"
                }
                """;

        mockMvc.perform(
                        post("/api/properties")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(propertyJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Integration Test Apartment"))
                .andExpect(jsonPath("$.location")
                        .value("Mumbai"))
                .andExpect(jsonPath("$.status")
                        .value("AVAILABLE"));

        mockMvc.perform(
                        get("/api/properties")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("Integration Test Apartment"))
                .andExpect(jsonPath("$[0].location")
                        .value("Mumbai"));
    }

    @Test
    void shouldReturnNotFoundForMissingProperty() throws Exception {

        mockMvc.perform(
                        get("/api/properties/999999")
                )
                .andExpect(status().isNotFound());
    }
}