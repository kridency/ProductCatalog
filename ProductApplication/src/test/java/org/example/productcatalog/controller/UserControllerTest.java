package org.example.productcatalog.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.productcatalog.dto.MessageDto;
import org.example.productcatalog.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.example.productcatalog.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collection;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class UserControllerTest extends AbstractTest {
    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("User account creation.")
    void givenNewUserCredentials_whenTryToCreateUser_thenReturnCorrectResult() throws Exception {
        String userString = "{ \"email\": \"test@hostname\", \"password\": \"test\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/identity")
                        .content(userString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(CREATED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("test@hostname"));
    }

    @Test
    @WithUserDetails(value = "name@hostname")
    @DisplayName("User account update.")
    void givenNewUserCredentials_whenTryToUpdateUser_thenReturnCorrectResult() throws Exception {
        String userString = "{ \"email\": \"name@hostname\", \"password\": \"test\" }";
        mockMvc.perform(MockMvcRequestBuilders.put("/identity")
                        .content(userString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(UPDATED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("name@hostname"));
    }

    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("User account delete.")
    void givenNewUserCredentials_whenTryToDeleteUser_thenReturnCorrectResult() throws Exception {
        String userString = "{ \"email\": \"name@hostname\", \"password\": \"test\" }";
        mockMvc.perform(MockMvcRequestBuilders.delete("/identity")
                        .content(userString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(DELETED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("name@hostname"));
    }

    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("List users.")
    void givenNewUserTemplate_whenTryToListUsers_thenReturnCorrectResult() throws Exception {
        String userString = "{ \"email\": \"name@hostname\" }";
        String result = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders
                        .get("/identity")
                        .content(userString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                .getContentAsByteArray(), MessageDto.class).getMessage();

        Assertions.assertTrue(objectMapper.readValue(result, new TypeReference<Collection<UserDto>>() {})
                        .stream().filter(x -> !x.getEmail().equals("name@hostname")).toList().isEmpty());
    }
}
