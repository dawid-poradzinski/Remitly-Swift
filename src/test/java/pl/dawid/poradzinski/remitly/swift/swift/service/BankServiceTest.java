package pl.dawid.poradzinski.remitly.swift.swift.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.dawid.poradzinski.remitly.swift.swift.repository.BankRepository;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;

@ExtendWith(MockitoExtension.class)
public class BankServiceTest {
    
    @Mock
    private BankRepository bankRepository;

    @InjectMocks
    private BankService bankService;

    private Bank bank0;

    private Bank bank1;

    @BeforeEach
    void setUp() {
        
        bank0 = new Bank("Bank A");
        bank1 = new Bank("Bank B");
    
    }

    @Test
    void shouldReturnExistingBanks() {

        // Given

        Set<Bank> banks = Set.of(bank0, bank1);
        when(bankRepository.findAll()).thenReturn(List.of(bank0, bank1));

        // When

        Set<Bank> result = bankService.getAllBanks();

        // Then

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(bank0));
        assertTrue(result.contains(bank1));
        assertEquals(banks, result);

    }

    @Test
    void shouldReturnAllNamesFromBanks() {

        List<Bank> banks = List.of(bank0, bank1);

        when(bankRepository.findAll()).thenReturn(banks);

        Set<String> result = bankService.getAllBanksName();

        assertNotNull(result);
        assertEquals(2, result.size());

        banks.stream().forEach(
            bank -> assertTrue(result.contains(bank.getName()))
        );


        verify(bankRepository).findAll();

    }

    @Test
    void shouldSaveAllBanks() {

        // Given

        List<Bank> banks = List.of(bank0, bank1);

        // When
        
        bankService.saveBanks(banks);

        // Then

        verify(bankRepository).saveAll(banks);
    }

}