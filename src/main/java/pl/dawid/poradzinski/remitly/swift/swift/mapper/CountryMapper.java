package pl.dawid.poradzinski.remitly.swift.swift.mapper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pl.dawid.poradzinski.remitly.swift.swift.dto.CountryDTO;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;

@Component
@RequiredArgsConstructor
public class CountryMapper {
    
    private final SwiftCodeMapper swiftCodeMapper;

    public CountryDTO entityToDTO(Country country) {
        
        return new CountryDTO(
            country.getCountryISO2(),
            country.getCountryName(),
            country.getSwiftCodes().stream().map( branch -> swiftCodeMapper.entityBranchToDTO(branch)).toList()
        );

    }
    
}
