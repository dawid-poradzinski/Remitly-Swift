package pl.dawid.poradzinski.remitly.swift.swift.dto;

import java.util.List;

public record CountryDTO (

    String countryISO2,
    String countryName,
    List<SwiftCodeDTO> swiftCodes
    
) {}
