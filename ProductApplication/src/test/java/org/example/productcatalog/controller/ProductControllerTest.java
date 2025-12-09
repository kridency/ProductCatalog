package org.example.productcatalog.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.productcatalog.AbstractTest;
import org.example.productcatalog.dto.MessageDto;
import org.example.productcatalog.dto.ProductDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collection;

import static org.example.productcatalog.preset.ProductCatalogInit.*;
import static org.example.productcatalog.preset.ProductCatalogInit.objectMapper;

public class ProductControllerTest extends AbstractTest {
    @Test
    @WithUserDetails(value = "name@hostname")
    @DisplayName("Product creation.")
    void givenNewProductDetails_whenTryToCreateProduct_thenReturnCorrectResult() throws Exception {
        String productString = "{ \"item\": \"I22\", " +
                "\"brand\": \"Puma\", " +
                "\"title\": \"Flip-flop\", " +
                "\"category\": \"Shoes\", " +
                "\"price\": 12.99 }";
        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                        .content(productString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(CREATED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("I22"));
    }


    @Test
    @WithUserDetails(value = "name@hostname")
    @DisplayName("Product update.")
    void givenNewProductDetails_whenTryToUpdateProduct_thenReturnCorrectResult() throws Exception {
        String productString = "{ \"item\": \"I21\", " +
                "\"brand\": \"Nike\", " +
                "\"title\": \"Moccasins\", " +
                "\"category\": \"Shoes\", " +
                "\"price\": 99.99 }";
        mockMvc.perform(MockMvcRequestBuilders.put("/product")
                        .content(productString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(UPDATED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("I21"));
    }

    @Test
    @WithUserDetails(value = "name@hostname")
    @DisplayName("Product delete.")
    void givenNewProductDetails_whenTryToDelete_thenReturnCorrectResult() throws Exception {
        String productString = "{ \"item\": \"I21\" }";
        mockMvc.perform(MockMvcRequestBuilders.delete("/product")
                        .content(productString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(DELETED))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("I21"));
    }

    @Test
    @WithUserDetails(value = "name@hostname")
    @DisplayName("List products.")
    void givenProductTemplate_whenTryToList_thenReturnCorrectResult() throws Exception {
        String productString = "{ \"item\": \"I11\" }";
        String result = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders
                        .get("/product")
                        .content(productString)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                .getContentAsByteArray(), MessageDto.class).getMessage();

        Assertions.assertTrue(objectMapper.readValue(result, new TypeReference<Collection<ProductDto>>() {})
                .stream().filter(x -> !x.getItem().equals("I11")).toList().isEmpty());
    }
}
