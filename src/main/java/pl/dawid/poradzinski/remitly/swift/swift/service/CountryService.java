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

    public Optional<CountryDTO> getAllDataByISO2(String ISO2) {

        return countryRepository.findById(ISO2).map(countryMapper::entityToDTOAll);

    }

    public Set<CountryDTO> getAllExistingCountries() {

        return countryRepository.findAll().stream().map(countryMapper::entityToDTOExceptSwiftCodes).collect(Collectors.toSet());

    }

    public void saveCountires(List<Country> countires) {

        countryRepository.saveAll(countires);

    }

    public Map<String, String> getAllCountriesAsMap() {
        
        // Return as Map. We don't need to sort it

        return countryRepository.findAll().stream()
            .collect(Collectors.toMap(Country::getISO2, Country::getName));

    }

}
