package pl.dawid.poradzinski.remitly.swift.swift.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.dawid.poradzinski.remitly.swift.swift.dto.CountryDTO;
import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;
import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

public class CountryMapperTest {
    
    private CountryMapper countryMapper;
    private SwiftCodeMapper swiftCodeMapper;

    @BeforeEach
    void setUp() {

        swiftCodeMapper = new SwiftCodeMapper();
        countryMapper = new CountryMapper(swiftCodeMapper);
    
    }

    @Test
    void testUppercaseSave() {

        // Given

        Country country = new Country();

        // When

        country.setISO2("pl");
        country.setName("poland");

        // Then

        assertEquals("PL", country.getISO2());
        assertEquals("POLAND", country.getName());

    }

    @Test
    void testMapCountryWithoutSwiftCodesToDTO() {

        // Given
        Country country = new Country();

        country.setISO2("PL");
        country.setName("POLAND");
        country.setSwiftCodes(null);

        // When

        CountryDTO result = countryMapper.entityToDTOAll(country);
        
        // Then

        assertNotNull(result);
        assertNotNull(result.swiftCodes());
        assertEquals(0, result.swiftCodes().size());

    }

    @Test
    void testMapCountryWithSwiftCodesToDTO() {

        // Given

        Country country = new Country();

        country.setISO2("PL");
        country.setName("POLAND");

        SwiftCode headquarterSwiftCode = new SwiftCode();

        headquarterSwiftCode.setAddress("Address");
        headquarterSwiftCode.setBank(new Bank("Bank"));
        headquarterSwiftCode.setBranches(null);
        headquarterSwiftCode.setCountry(country);
        headquarterSwiftCode.setHeadquarter(null);
        headquarterSwiftCode.setIsHeadquarter(true);
        headquarterSwiftCode.setSwiftCode("AAABBBCCXXX");

        country.setSwiftCodes(Set.of(headquarterSwiftCode));

        // Then

        CountryDTO result = countryMapper.entityToDTOAll(country);
        
        assertNotNull(result);
        assertNotNull(result.swiftCodes());
        assertEquals(1, result.swiftCodes().size());

    }

    @Test
    void testShowingNoBranchesInSwiftCodesWithBranches() {

        // Given

        Country country = new Country();

        country.setISO2("PL");
        country.setName("POLAND");

        SwiftCode headquarterSwiftCode = new SwiftCode();

        headquarterSwiftCode.setAddress("Address");
        headquarterSwiftCode.setBank(new Bank("Bank"));
        headquarterSwiftCode.setBranches(null);
        headquarterSwiftCode.setCountry(country);
        headquarterSwiftCode.setHeadquarter(null);
        headquarterSwiftCode.setIsHeadquarter(true);
        headquarterSwiftCode.setSwiftCode("AAABBBCCXXX");

        SwiftCode branchSwiftCode = new SwiftCode();

        branchSwiftCode.setAddress("Address");
        branchSwiftCode.setBank(new Bank("Bank"));
        branchSwiftCode.setBranches(null);
        branchSwiftCode.setCountry(country);
        branchSwiftCode.setHeadquarter(headquarterSwiftCode);
        branchSwiftCode.setIsHeadquarter(false);
        branchSwiftCode.setSwiftCode("AAABBBCC001");

        headquarterSwiftCode.setBranches(Set.of(branchSwiftCode));

        // To show only headquarter

        country.setSwiftCodes(Set.of(headquarterSwiftCode));

        // When

        CountryDTO result = countryMapper.entityToDTOAll(country);

        // Then

        assertNotNull(result);
        assertEquals(1, result.swiftCodes().size());

        SwiftCodeDTO swiftCodeDTO = result.swiftCodes().get(0);
        
        assertNotNull(swiftCodeDTO);
        assertNull(swiftCodeDTO.branches());

    }

    @Test
    void testMapCountryToDTOExceptSwiftCodes() {

        // Given

        Country country = new Country();

        country.setISO2("PL");
        country.setName("POLAND");

        SwiftCode headquarterSwiftCode = new SwiftCode();

        headquarterSwiftCode.setAddress("Address");
        headquarterSwiftCode.setBank(new Bank("Bank"));
        headquarterSwiftCode.setBranches(null);
        headquarterSwiftCode.setCountry(country);
        headquarterSwiftCode.setHeadquarter(null);
        headquarterSwiftCode.setIsHeadquarter(true);
        headquarterSwiftCode.setSwiftCode("AAABBBCCXXX");

        SwiftCode branchSwiftCode = new SwiftCode();

        branchSwiftCode.setAddress("Address");
        branchSwiftCode.setBank(new Bank("Bank"));
        branchSwiftCode.setBranches(null);
        branchSwiftCode.setCountry(country);
        branchSwiftCode.setHeadquarter(headquarterSwiftCode);
        branchSwiftCode.setIsHeadquarter(false);
        branchSwiftCode.setSwiftCode("AAABBBCC001");

        headquarterSwiftCode.setBranches(Set.of(branchSwiftCode));

        country.setSwiftCodes(Set.of(headquarterSwiftCode,branchSwiftCode));

        // When

        CountryDTO result = countryMapper.entityToDTOExceptSwiftCodes(country);

        // Then

        assertNotNull(result);
        assertEquals("PL", result.countryISO2());
        assertEquals("POLAND", result.countryName());
        assertNull(result.swiftCodes());

    }

}
