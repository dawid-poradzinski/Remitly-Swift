package pl.dawid.poradzinski.remitly.swift.swift.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;

public interface BankRepository extends JpaRepository<Bank, String> {
    
}
