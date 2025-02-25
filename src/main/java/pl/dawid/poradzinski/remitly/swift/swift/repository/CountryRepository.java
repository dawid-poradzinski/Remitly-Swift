package pl.dawid.poradzinski.remitly.swift.swift.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;

public interface CountryRepository extends JpaRepository<Country, String> {
    
    Optional<Country> findByISO2OrName(String ISO2, String name);

}
