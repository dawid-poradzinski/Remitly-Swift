package pl.dawid.poradzinski.remitly.swift.swift.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

public interface SwiftCodeRepository extends JpaRepository<SwiftCode, String> {
    
    List<SwiftCode> findByIsHeadquarter(Boolean isHeadquarter);

    List<SwiftCode> findBySwiftCodeStartingWithAndIsHeadquarterFalse(String keyword);

}
