package org.example.productcatalog.controller;

import org.springframework.http.MediaType;
import org.example.productcatalog.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.example.productcatalog.preset.ProductCatalogInit.CREATED;

public class UserControllerTest extends AbstractTest {
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("User account creation.")
    void givenNewUserCredentials_whenTryToCreate_thenReturnCorrectResult() throws Exception {
        String userString = "{ \"email\": \"test@hostname\", \"password\": \"test\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/create")
                        .content(userString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(CREATED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("test@hostname"));
    }

}
