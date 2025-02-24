package pl.dawid.poradzinski.remitly.swift.swift.mapper;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pl.dawid.poradzinski.remitly.swift.swift.dto.CountryDTO;
import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;

@Component
@RequiredArgsConstructor
public class CountryMapper {
    
    private final SwiftCodeMapper swiftCodeMapper;

    public CountryDTO entityToDTOAll(Country country) {
        
        return new CountryDTO (
            country.getISO2(),
            country.getName(),
            country.getSwiftCodes() == null ? List.of() : country.getSwiftCodes().stream().
            map( branch -> swiftCodeMapper.entityInsideOtherToDTO(branch)).
            sorted(Comparator.comparing(SwiftCodeDTO::swiftCode)).
            toList()
        );

    }
    
    public CountryDTO entityToDTOExceptSwiftCodes(Country country) {

        return new CountryDTO (
            country.getISO2(), country.getName(), null
        );

    }
    
}
