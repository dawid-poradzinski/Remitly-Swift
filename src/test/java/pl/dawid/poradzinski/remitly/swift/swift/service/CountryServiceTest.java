package pl.dawid.poradzinski.remitly.swift.swift.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.dawid.poradzinski.remitly.swift.swift.dto.CountryDTO;
import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.mapper.CountryMapper;
import pl.dawid.poradzinski.remitly.swift.swift.repository.CountryRepository;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;
import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {
    
    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryService countryService;

    private Country testCountry;

    @BeforeEach
    void setUp() {

        testCountry = new Country();
        testCountry.setISO2("PL");
        testCountry.setName("POLAND");

        SwiftCode haedquarter = new SwiftCode();
        haedquarter.setAddress("Address");
        haedquarter.setBank(new Bank("Bank"));
        haedquarter.setCountry(testCountry);
        haedquarter.setIsHeadquarter(true);
        haedquarter.setSwiftCode("AAABBBCCXXX");

        SwiftCode branch = new SwiftCode();
        branch.setAddress("Address");
        branch.setBank(new Bank("Bank"));
        branch.setCountry(testCountry);
        branch.setIsHeadquarter(false);
        branch.setSwiftCode("AAABBBCC000");
        branch.setHeadquarter(haedquarter);

        haedquarter.setBranches(Set.of(branch));

        testCountry.setSwiftCodes(Set.of(haedquarter, branch));
    }

    @Test
    void shouldReturnCountryByISO2() {

        // Given

        SwiftCodeDTO expectedHeadquarter = new SwiftCodeDTO("Address", "Bank", "PL", true, "AAABBBCCXXX");
        SwiftCodeDTO expectedBranch = new SwiftCodeDTO("Address", "Bank", "PL", false, "AAABBBCC000");

        CountryDTO expectedCountry = new CountryDTO("PL", "POLAND", List.of(expectedHeadquarter, expectedBranch));

        when(countryRepository.findById("PL")).thenReturn(Optional.of(testCountry));
        when(countryMapper.entityToDTOAll(testCountry)).thenReturn(expectedCountry);

        // When

        Optional<CountryDTO> optional = countryService.getAllDataByISO2("PL");

        // Then

        assertTrue(optional.isPresent());
        verify(countryRepository).findById("PL");
        verify(countryMapper).entityToDTOAll(any(Country.class));  

        CountryDTO result = optional.get();

        assertEquals("PL", result.countryISO2());  
        assertEquals("POLAND", result.countryName());  
        assertNotNull(result.swiftCodes());  
        assertEquals(2, result.swiftCodes().size()); 


        assertEquals("AAABBBCCXXX", result.swiftCodes().get(0).swiftCode());
        assertEquals("AAABBBCC000", result.swiftCodes().get(1).swiftCode());

    }

    @Test
    void shouldReturnCountryWithEmptySwiftCodesByISO2() {

        // Given

        testCountry.setSwiftCodes(Set.of());

        CountryDTO expectedCountry = new CountryDTO("PL", "POLAND", List.of());

        when(countryRepository.findById("PL")).thenReturn(Optional.of(testCountry));
        when(countryMapper.entityToDTOAll(testCountry)).thenReturn(expectedCountry);

        // When

        Optional<CountryDTO> optional = countryService.getAllDataByISO2("PL");

        // Then

        assertTrue(optional.isPresent());
        verify(countryRepository).findById("PL");
        verify(countryMapper).entityToDTOAll(any(Country.class));

        CountryDTO result = optional.get();

        assertNotNull(result.swiftCodes());
        assertEquals(0, result.swiftCodes().size());

    }

    @Test
    void shouldReturnEmptyOptionalWhenCountryNotFound() {

        // Given

        when(countryRepository.findById("PL")).thenReturn(Optional.empty());

        // When

        Optional<CountryDTO> optional = countryService.getAllDataByISO2("PL");

        // Then

        assertTrue(optional.isEmpty());

        verify(countryRepository).findById("PL");
        verify(countryMapper, times(0)).entityToDTOAll(any(Country.class));

    }

    @Test
    void shouldReturnEmptyListWhenNoCountriesExist() {

        // Given
        
        when(countryRepository.findAll()).thenReturn(List.of());
    
        // When

        Set<CountryDTO> result = countryService.getAllExistingCountries();
    
        // Then

        assertNotNull(result);
        assertTrue(result.isEmpty());

    }
    
    @Test
    void shouldReturnISO2AndNameListWhenCountriesExist() {

        // Given

        Country testCountry2 = new Country();
        testCountry2.setISO2("US");
        testCountry2.setName("USA");
    
        when(countryRepository.findAll()).thenReturn(List.of(testCountry, testCountry2));
        when(countryMapper.entityToDTOExceptSwiftCodes(any(Country.class))).thenReturn(new CountryDTO("PL", "POLAND", null), new CountryDTO("US", "USA", null));
    
        // When

        List<CountryDTO> result = countryService.getAllExistingCountries().stream().toList();
    
        // Then

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("US", result.get(0).countryISO2());
        assertEquals("PL", result.get(1).countryISO2());
    }

}
