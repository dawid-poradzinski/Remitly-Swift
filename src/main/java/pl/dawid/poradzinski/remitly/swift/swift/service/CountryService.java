package pl.dawid.poradzinski.remitly.swift.swift.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pl.dawid.poradzinski.remitly.swift.swift.dto.CountryDTO;
import pl.dawid.poradzinski.remitly.swift.swift.mapper.CountryMapper;
import pl.dawid.poradzinski.remitly.swift.swift.repository.CountryRepository;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;


@Service
@RequiredArgsConstructor
public class CountryService {
    
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    /**
     * Retrieves country information along with associated Swift codes.
     *
     * @param ISO2 the ISO2 country code
     * @return {@code Optional<CountryDTO>} containing the country details if found, otherwise empty
     */
    public Optional<CountryDTO> getAllDataByISO2(String ISO2) {

        return countryRepository.findById(ISO2).map(countryMapper::entityToDTOAll);

    }

    /**
     * Retrieves a set of all existing countries without associated Swift codes.
     * 
     * @return {@code Set<CountryDTO>} containing country details excluding Swift codes
     */
    public Set<CountryDTO> getAllExistingCountries() {

        return countryRepository.findAll().stream().map(countryMapper::entityToDTOExceptSwiftCodes).collect(Collectors.toSet());

    }

    /**
     * Saves a list of countries to database.
     * 
     * @param countires list of countries to be saved
     */
    public void saveCountires(List<Country> countires) {

        countryRepository.saveAll(countires);

    }

    /**
     * Retrieves a Map of all existing countiries.
     * 
     * @return A map containing country ISO2 codes as keys and their names as values
     */
    public Map<String, String> getAllCountriesAsMap() {
        
        return countryRepository.findAll().stream()
            .collect(Collectors.toMap(Country::getISO2, Country::getName));

    }

    /**
     * Saves a given country entity to the database.
     *
     * @param country the country entity to be saved
     */
    public void saveCountry(Country country) {
        countryRepository.save(country);
    }

    /**
     * Retrieves the first country that matches the given ISO2 code or name.
     *
     * @param iso2 the ISO2 country code to search for
     * @param name the country name to search for
     * @return {@code Optional<Country>} containing the matching country if found, otherwise empty
     */
    public Optional<Country> getByISO2OrNameLimit1(String iso2, String name) {

        return countryRepository.findFirstByISO2OrName(iso2, name);

    }

}
