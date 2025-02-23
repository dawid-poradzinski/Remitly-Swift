package pl.dawid.poradzinski.remitly.swift.swift.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pl.dawid.poradzinski.remitly.swift.swift.dto.CountryDTO;
import pl.dawid.poradzinski.remitly.swift.swift.mapper.CountryMapper;
import pl.dawid.poradzinski.remitly.swift.swift.repository.CountryRepository;


@Service
@RequiredArgsConstructor
public class CountryService {
    
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public Optional<CountryDTO> getAllDataByISO2(String ISO2) {

        return countryRepository.findById(ISO2).map(countryMapper::entityToDTOAll);

    }

    public List<CountryDTO> getAllExistingCountries() {

        return countryRepository.findAll().stream().map(countryMapper::entityToDTOExceptSwiftCodes).toList();

    }

}
