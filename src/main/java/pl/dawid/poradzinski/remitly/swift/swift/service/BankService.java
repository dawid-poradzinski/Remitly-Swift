package pl.dawid.poradzinski.remitly.swift.swift.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pl.dawid.poradzinski.remitly.swift.swift.repository.BankRepository;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;

@Service
@RequiredArgsConstructor
public class BankService {
    
    private final BankRepository bankRepository;

    /**
     * Retrieves a set of all existing banks entities
     * 
     * @return {@code Set<Bank>} List of banks in database
     */
    public Set<Bank> getAllBanks() {

        return bankRepository.findAll().stream().collect(Collectors.toSet());

    }

    /**
     * Retrieves a set of all existing banks names
     * 
     * @return {@code Set<String>} List of banks names in database
     */
    public Set<String> getAllBanksName() {

        return getAllBanks().stream().map(Bank::getName).collect(Collectors.toSet());

    }

    /**
     * Save single Bank entity to database
     * 
     * @param bank Entity to be saved
     */
    public void saveBank(Bank bank) {

        bankRepository.save(bank);

    }

    /**
     * Save list of bank entities to database
     * 
     * @param banks list of bank entities
     */
    public void saveBanks(List<Bank> banks) {
        
        bankRepository.saveAll(banks);

    }

}
