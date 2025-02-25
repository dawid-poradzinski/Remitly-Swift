package pl.dawid.poradzinski.remitly.swift.swift.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.HashMap;
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

import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.exception.InvalidFileFormatException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.SwiftCodeAlreadyExistException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.SwiftCodeDoesntExistException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.SwiftCodeIsHeadquarterException;
import pl.dawid.poradzinski.remitly.swift.swift.mapper.SwiftCodeMapper;
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

    @Mock
    private SwiftCodeMapper swiftCodeMapper;

    @Mock
    private CountryService countryService;

    @Mock
    private BankService bankService;

    @InjectMocks
    private SwiftCodeService swiftCodeService;


    private SwiftCode headquarter;
    private SwiftCode branch;

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
        when(countryService.getAllCountriesAsMap()).thenReturn(new HashMap<>());


        // When

        swiftCodeService.saveExcelToDatabase(mockFile);

        // Then

        verify(excelUploadService).mapExcelToDatabaseEntities(any(InputStream.class));
        verify(swiftCodeRepository).saveAll(anyList());
        assertEquals(headquarter, branch.getHeadquarter());

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
    void shouldReturnDTOIfExist() {

        // Given

        when(swiftCodeRepository.findById("AAABBBCCXXX")).thenReturn(Optional.of(headquarter));

        SwiftCodeDTO dto = new SwiftCodeDTO("Address", "Bank", "PL", true, "AAABBBCCXXX");

        when(swiftCodeMapper.entityAsMainToDTO(headquarter, true)).thenReturn( dto );

        // When

        Optional<SwiftCodeDTO> optional = swiftCodeService.getBySwiftCode("AAABBBCCXXX");

        assertTrue(optional.isPresent());
        verify(swiftCodeRepository).findById("AAABBBCCXXX");
        verify(swiftCodeMapper).entityAsMainToDTO(headquarter, true);

    }

    @Test
    void shouldReturnEmptyIfDoesntExist() {

        // Given

        when(swiftCodeRepository.findById("AAABBBCC001")).thenReturn(Optional.empty());

        Optional<SwiftCodeDTO> optional = swiftCodeService.getBySwiftCode("AAABBBCC001");

        assertTrue(optional.isEmpty());
        verify(swiftCodeRepository).findById("AAABBBCC001");
        verify(swiftCodeMapper, never()).entityAsMainToDTO(any(SwiftCode.class), any(Boolean.class));

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

        assertNull(branch.getHeadquarter());

       
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
    
           
    }

    @Test
    void shouldThrowExceptionOnDeleteSwiftCodeIfDoesntExist() {

        // Given

        when(swiftCodeRepository.findById("AAABBBCCXXX")).thenReturn(Optional.empty());

        // When & Then

        assertThrows(SwiftCodeDoesntExistException.class, () -> swiftCodeService.deleteBySwiftCode("AAABBBCCXXX"));

        verify(swiftCodeRepository, never()).delete(any(SwiftCode.class));
    }

    @Test
    void shouldAddConnectionBetweenHeadquarterAndBranchifSwiftCodeMatch() {

        branch.setHeadquarter(null);
        headquarter.setBranches(null);

        when(swiftCodeRepository.findByIsHeadquarter(true)).thenReturn(List.of(headquarter));
        when(swiftCodeRepository.findByIsHeadquarter(false)).thenReturn(List.of(branch));

        swiftCodeService.addConnectionBetweenBranchAndHeadquarter();

        assertEquals(headquarter, branch.getHeadquarter());

    }

    @Test
    void shouldNotAddConnectioNBetweenHeadquarterAndBranchIfSwiftCodeDoesntMatch() {

        branch.setHeadquarter(null);
        branch.setSwiftCode("BBBAAACC000");

        when(swiftCodeRepository.findByIsHeadquarter(true)).thenReturn(List.of(headquarter));
        when(swiftCodeRepository.findByIsHeadquarter(false)).thenReturn(List.of(branch));

        swiftCodeService.addConnectionBetweenBranchAndHeadquarter();

        assertEquals(null, branch.getHeadquarter());
    }


    @Test
    void shouldThrowExceptionIfSwiftCodeAlreadyExistInDb() {

        // Given
        
        when(swiftCodeRepository.existsById(headquarter.getSwiftCode())).thenReturn(true);

        SwiftCodeDTO dto = new SwiftCodeDTO("Address", "Bank", "PL", "POLAND", true, headquarter.getSwiftCode());

        // When & Then

        assertThrows(SwiftCodeAlreadyExistException.class, () -> swiftCodeService.addNewSwiftCode(dto));

    }

    @Test
    void shouldThrowExceptionIfIsHeadquarterIsntEqualToEndsWithXXX() {

        // Given

        when(swiftCodeRepository.existsById(headquarter.getSwiftCode())).thenReturn(false);

        SwiftCodeDTO dto = new SwiftCodeDTO("Address", "Bank", "PL", "POLAND", false, headquarter.getSwiftCode());

        headquarter.setIsHeadquarter(false);

        when(swiftCodeMapper.dtoToEntity(dto)).thenReturn(headquarter);

        // When & Then

        assertThrows(SwiftCodeIsHeadquarterException.class, () -> swiftCodeService.addNewSwiftCode(dto));

    }

    @Test
    void shouldSaveDTOToDbIfDoesntExistInIt() {

        // Given

        when(swiftCodeRepository.existsById(headquarter.getSwiftCode())).thenReturn(false);

        SwiftCodeDTO dto = new SwiftCodeDTO("Address", "Bank", "PL", "POLAND", true, headquarter.getSwiftCode());

        when(swiftCodeMapper.dtoToEntity(dto)).thenReturn(headquarter);

        // When

        swiftCodeService.addNewSwiftCode(dto);

        // Then

        verify(swiftCodeMapper).dtoToEntity(dto);
        verify(swiftCodeRepository).save(headquarter);

    }

    @Test
    void shouldCheckForHeadquarterInDbAndAddItIfExistAndThenSave() {

        // Given

        when(swiftCodeRepository.existsById(branch.getSwiftCode())).thenReturn(false);

        SwiftCodeDTO dto = new SwiftCodeDTO("Address", "Bank", "PL", "POLAND", false, branch.getSwiftCode());

        branch.setHeadquarter(null);
        when(swiftCodeMapper.dtoToEntity(dto)).thenReturn(branch);

        when(swiftCodeRepository.findById(branch.getSwiftCode().substring(0,branch.getSwiftCode().length()-3)+"XXX")).thenReturn(Optional.of(headquarter));

        when(swiftCodeRepository.save(any(SwiftCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When

        SwiftCode swiftCode = swiftCodeService.addNewSwiftCode(dto);
        verify(swiftCodeRepository).existsById(branch.getSwiftCode());
        verify(swiftCodeRepository).findById(headquarter.getSwiftCode());
        assertNotNull(swiftCode.getHeadquarter());
        verify(swiftCodeRepository).save(swiftCode);

    }

    @Test
    void shouldCheckForBranchesInDbAndAddHeadquarterForThemIfTheyExistAndThenSave() {

        // Given

        when(swiftCodeRepository.existsById(headquarter.getSwiftCode())).thenReturn(false);

        SwiftCodeDTO dto = new SwiftCodeDTO("Address", "Bank", "PL", "POLAND", true, headquarter.getSwiftCode());

        branch.setHeadquarter(null);

        when(swiftCodeMapper.dtoToEntity(dto)).thenReturn(headquarter);

        when(swiftCodeRepository.findBySwiftCodeStartingWithAndIsHeadquarterFalse(headquarter.getSwiftCode().substring(0,headquarter.getSwiftCode().length()-3))).thenReturn(List.of(branch));
   
        // When

        swiftCodeService.addNewSwiftCode(dto);

        // Then

        verify(swiftCodeRepository).existsById(headquarter.getSwiftCode());
        verify(swiftCodeMapper).dtoToEntity(dto);
        verify(swiftCodeRepository).findBySwiftCodeStartingWithAndIsHeadquarterFalse("AAABBBCC");
        assertNotNull(branch.getHeadquarter());

    }

}
