package pl.dawid.poradzinski.remitly.swift.swift.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SwiftCodeDTO (

    @NotBlank(message = "Address cannot be blank")
    String address,
    @NotBlank(message = "Bank name cannot be blank")
    String bankName,
    @NotBlank(message = "Country ISO2 cannot be blank")
    String countryISO2,
    @NotBlank(message = "Country name cannot be blank")
    String countryName,
    @NotNull(message = "isHeadquarter must not be null")
    Boolean isHeadquarter,
    @NotBlank(message = "Swift code cannot be blank")
    @Size(min = 11, max = 11, message = "Swift code must be exactly 11 characters")
    String swiftCode,
    @Valid
    List<SwiftCodeDTO> branches
    
) {

    public SwiftCodeDTO(String address, String bankName, String countryISO2, boolean isHeadquarter, String swiftCode) {

        this(address, bankName, countryISO2, null, isHeadquarter, swiftCode, null);
        
    }

    public SwiftCodeDTO(String address, String bankName, String countryISO2, String countryName, boolean isHeadquarter, String swiftCode) {

        this(address, bankName, countryISO2, countryName, isHeadquarter, swiftCode, null);

    }

}
