package pl.dawid.poradzinski.remitly.swift.swift.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SwiftCodeDTO (

    String address,
    String bankName,
    String countryISO2,
    String countryName,
    Boolean isHeadquarter,
    String swiftCode,
    List<SwiftCodeDTO> branches
    
) {

    public SwiftCodeDTO(String address, String bankName, String countryISO2, boolean isHeadquarter, String swiftCode) {

        this(address, bankName, countryISO2, null, isHeadquarter, swiftCode, null);
        
    }

    public SwiftCodeDTO(String address, String bankName, String countryISO2, String countryName, boolean isHeadquarter, String swiftCode) {

        this(address, bankName, countryISO2, countryName, isHeadquarter, swiftCode, null);

    }

}
