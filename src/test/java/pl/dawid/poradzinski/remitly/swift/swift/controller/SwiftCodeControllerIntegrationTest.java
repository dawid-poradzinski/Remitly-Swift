package pl.dawid.poradzinski.remitly.swift.swift.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import pl.dawid.poradzinski.remitly.swift.swift.dto.CountryDTO;
import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.service.CountryService;
import pl.dawid.poradzinski.remitly.swift.swift.service.SwiftCodeService;

@WebMvcTest(SwiftCodeController.class)
public class SwiftCodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CountryService countryService;

    @MockitoBean
    private SwiftCodeService swiftCodeService;

    @Test
    void returnAllSwiftCodesForSpecificCountry_ShouldReturnCountryDTO_WhenDataExists() throws Exception {

        String countryISO2 = "PL";
        CountryDTO countryDTO = new CountryDTO(countryISO2, "POLAND", null);
        
        when(countryService.getAllDataByISO2(anyString())).thenReturn(Optional.of(countryDTO));
        
        mockMvc.perform(get("/v1/swift-codes/country/{countryISO2Code}", countryISO2))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.countryISO2").value("PL"))
               .andExpect(jsonPath("$.countryName").value("POLAND")); 

    }

    @Test
    void returnAllSwiftCodesForSpecificCountry_ShouldReturnNotFound_WhenNoDataExists() throws Exception {

        when(countryService.getAllDataByISO2(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/swift-codes/country/{countryISO2Code}", "XX"))
               .andExpect(status().isNotFound());

    }
    
    @Test
    void returnSwiftCodeDTOForSpecificSwift_ShouldReturnSwiftCodeDTO_WhenDataExists() throws Exception {

        String swiftCode = "ABC123";
        SwiftCodeDTO swiftCodeDTO = new SwiftCodeDTO("Address", "Bank", "PL", false, swiftCode);
        
        when(swiftCodeService.getBySwiftCode(anyString())).thenReturn(Optional.of(swiftCodeDTO));

        mockMvc.perform(get("/v1/swift-codes/{swiftCode}", swiftCode))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.swiftCode").value("ABC123"));

    }

    @Test
    void returnSwiftCodeDTOForSpecificSwift_ShouldReturnNotFound_WhenNoDataExists() throws Exception {

        when(swiftCodeService.getBySwiftCode(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/swift-codes/{swiftCode}", "XYZ789"))
               .andExpect(status().isNotFound());

    }
    
}