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

    public void saveBank(Bank bank) {
        bankRepository.save(bank);
    }

    public Set<Bank> getAllBanks() {

        return bankRepository.findAll().stream().collect(Collectors.toSet());

    }

    public Set<String> getAllBanksName() {

        return getAllBanks().stream().map(Bank::getName).collect(Collectors.toSet());

    }

    public void saveBanks(List<Bank> banks) {
        
        bankRepository.saveAll(banks);

    }

}
