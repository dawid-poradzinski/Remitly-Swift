package pl.dawid.poradzinski.remitly.swift.swift.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import pl.dawid.poradzinski.remitly.swift.swift.exception.InvalidFileFormatException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.SwiftCodeDoesntExistException;
import pl.dawid.poradzinski.remitly.swift.swift.repository.SwiftCodeRepository;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;
import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

@ExtendWith(MockitoExtension.class)
public class SwiftCodeServiceTest {
    
    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @Mock
    private ExcelUploadService excelUploadService;

    @InjectMocks
    private SwiftCodeService swiftCodeService;


    private SwiftCode headquarter;
    private SwiftCode branch;

    // Saving to DB from Excel
    @BeforeEach
    void setUp() {

        Country country = new Country();

        country.setISO2("PL");
        country.setName("POLAND");

        headquarter = new SwiftCode();
        headquarter.setAddress("Address");
        headquarter.setBank(new Bank("Bank"));
        headquarter.setCountry(country);
        headquarter.setHeadquarter(null);
        headquarter.setIsHeadquarter(true);
        headquarter.setSwiftCode("AAABBBCCXXX");

        branch = new SwiftCode();
        branch.setAddress("Address");
        branch.setBank(new Bank("Bank"));
        branch.setCountry(country);
        branch.setHeadquarter(headquarter);
        branch.setIsHeadquarter(false);
        branch.setSwiftCode("AAABBBCC000");
        branch.setBranches(null);

        Set<SwiftCode> branches = new HashSet<>();
        branches.add(branch);

        headquarter.setBranches(branches);

    }

    @Test
    void shouldSave() {

        // Given

        List<SwiftCode> swiftCodes = List.of(headquarter,branch);

        // When

        swiftCodeService.saveSwiftCodes(swiftCodes);

        // Then

        verify(swiftCodeRepository).saveAll(swiftCodes);


    }

    @Test
    void shouldSaveToDBFromExcel() {

        // Given

        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test.xlsx",
            "application/vnd.ms-excel",
            new byte[0]
        );

        when(excelUploadService.isValidExcelFile(mockFile)).thenReturn(true);
        when(excelUploadService.mapExcelToDatabaseEntities(any(InputStream.class))).thenReturn(List.of(headquarter,branch));

        // When

        swiftCodeService.saveExcelToDatabase(mockFile);

        // Then

        verify(excelUploadService).mapExcelToDatabaseEntities(any(InputStream.class));
        verify(swiftCodeRepository).saveAll(anyList());

    }

    @Test
    void shouldThrowExceptionWhenInvalidExcelFileProvided() {
        
        // Given

        MultipartFile invalidFile = mock(MultipartFile.class);

        when(excelUploadService.isValidExcelFile(invalidFile)).thenReturn(false);

        // When & Then

        assertThrows(InvalidFileFormatException.class, () -> swiftCodeService.saveExcelToDatabase(invalidFile));

        verify(excelUploadService, never()).mapExcelToDatabaseEntities(any(InputStream.class));
    }


    @Test
    void shouldDeleteSwiftCodeOfHeadquarterWithoutBranches() {

        headquarter.setBranches(null);

        // Given

        when(swiftCodeRepository.findById("AAABBBCCXXX")).thenReturn(Optional.of(headquarter));

        // When

        swiftCodeService.deleteBySwiftCode("AAABBBCCXXX");

        // Then

        verify(swiftCodeRepository).findById("AAABBBCCXXX");
        verify(swiftCodeRepository).delete(headquarter);

        // Don't save branches to delete Headquarter
        // Don't save headquarter, to delete from Branch

        verify(swiftCodeRepository, never()).saveAll(Set.of(branch));
        verify(swiftCodeRepository, never()).save(headquarter);

    }

    @Test
    void shouldDeleteSwiftCodeOfHeadquarterWithBranches() {

        // Given

        when(swiftCodeRepository.findById("AAABBBCCXXX")).thenReturn(Optional.of(headquarter));

        // When

        swiftCodeService.deleteBySwiftCode("AAABBBCCXXX");

        // Then

        verify(swiftCodeRepository).findById("AAABBBCCXXX");
        verify(swiftCodeRepository).delete(headquarter);

        // Save branches to delete Headquarter
        // Don't save headquarter, to delete from Branch

        verify(swiftCodeRepository).saveAll(Set.of(branch));
        verify(swiftCodeRepository, never()).save(headquarter);
        assertNull(branch.getHeadquarter());
        
    }

    @Test
    void shouldDeleteSwiftCodeOfBranchWithHeadquarter() {

        // Given

        when(swiftCodeRepository.findById("AAABBBCC000")).thenReturn(Optional.of(branch));

        // When

        swiftCodeService.deleteBySwiftCode("AAABBBCC000");

        // Then

        verify(swiftCodeRepository).findById("AAABBBCC000");
        verify(swiftCodeRepository).delete(branch);

        // Don't save branches to delete Headquarter
        // Save headquarter, to delete from Branch

        verify(swiftCodeRepository, never()).saveAll(anyList());
        verify(swiftCodeRepository).save(headquarter);
        verify(swiftCodeRepository).delete(branch);

        assertFalse(headquarter.getBranches().contains(branch));
    }

    @Test
    void shouldDeleteSwiftCodeOfbranchWithoutHeadquarter() {

            // Given

            branch.setHeadquarter(null);

            when(swiftCodeRepository.findById("AAABBBCC000")).thenReturn(Optional.of(branch));

            // When
    
            swiftCodeService.deleteBySwiftCode("AAABBBCC000");
    
            // Then
    
            verify(swiftCodeRepository).findById("AAABBBCC000");
            verify(swiftCodeRepository).delete(branch);
    
            // Don't save branches to delete Headquarter
            // Don't save headquarter, to delete from Branch
    
            verify(swiftCodeRepository, never()).saveAll(anyList());
            verify(swiftCodeRepository, never()).save(headquarter);
            verify(swiftCodeRepository).delete(branch);

    }

    @Test
    void shouldThrowExceptionOnDeleteSwiftCodeIfDoesntExist() {

        // Given

        when(swiftCodeRepository.findById("AAABBBCCXXX")).thenReturn(Optional.empty());

        // When & Then

        assertThrows(SwiftCodeDoesntExistException.class, () -> swiftCodeService.deleteBySwiftCode("AAABBBCCXXX"));

        verify(swiftCodeRepository, never()).delete(any(SwiftCode.class));
    }

}
