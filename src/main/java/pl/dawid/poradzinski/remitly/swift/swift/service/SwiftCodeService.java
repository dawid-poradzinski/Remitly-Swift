package pl.dawid.poradzinski.remitly.swift.swift.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.exception.CountryConflictException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.InvalidFileFormatException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.SwiftCodeAlreadyExistException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.SwiftCodeDoesntExistException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.SwiftCodeIsHeadquarterException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.SwiftCodeToShortException;
import pl.dawid.poradzinski.remitly.swift.swift.mapper.SwiftCodeMapper;
import pl.dawid.poradzinski.remitly.swift.swift.repository.SwiftCodeRepository;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;
import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

@Service
@RequiredArgsConstructor
public class SwiftCodeService {
    
    private final SwiftCodeRepository swiftCodeRepository;
    private final ExcelUploadService excelUploadService;
    private final SwiftCodeMapper swiftCodeMapper;
    private final CountryService countryService;
    private final BankService bankService;

    /**
     * Saves list of Swift codes to database.
     *  
     * @param swiftCodes list of Swift codes
     */
    public void saveSwiftCodes(List<SwiftCode> swiftCodes) {

        swiftCodeRepository.saveAll(swiftCodes);

    }

    /**
     * Maps excel data to Swift code entities, validate and save them to database
     * 
     * @param file from with data will be retrived
     * @return number of saved entities
     */
    public int saveExcelToDatabase(MultipartFile file) {

        if(excelUploadService.isValidExcelFile(file)) {

            try {
               
                List<SwiftCode> excelSwiftCodes = excelUploadService.mapExcelToDatabaseEntities(file.getInputStream());

                excelSwiftCodes = validateSwiftCodeList(excelSwiftCodes);

                saveSwiftCodes(excelSwiftCodes);

                addConnectionBetweenBranchsAndHeadquarterInDatabase();

                return excelSwiftCodes.size();

            } catch (Exception e) {

                
            }

        }
        else {

            throw new InvalidFileFormatException("Expected excel file");

        }

        return 0;

    }


    private List<SwiftCode> validateSwiftCodeList(List<SwiftCode> swiftCodes) {

        Map<String,String> existingCountries = countryService.getAllCountriesAsMap();
        List<Country> newCountries = new ArrayList<>();

        List<Bank> banks = new ArrayList<>();

        swiftCodes.removeIf(swiftCode -> {

            Country country = swiftCode.getCountry();
            String iso2 = country.getISO2();
            String name = country.getName();

            if(existingCountries.containsKey(iso2)) {

                return !existingCountries.get(iso2).equals(name);

            }

            if(existingCountries.containsValue(name)) {

                return true;

            }

            existingCountries.put(iso2, name);
            newCountries.add(country);

            return false;
        });

        countryService.saveCountires(newCountries);
        bankService.saveBanks(banks);

        return swiftCodes;
    }


    /**
     * Retrieves Swift code by its code. Map to headquarter or branch DTO.
     * @param swift code, to look in database
     * @return {@code Optional<SwiftCodeDTO>} if found, or else empty
     */
    public Optional<SwiftCodeDTO> getBySwiftCode(String swift) {

        return swiftCodeRepository.findById(swift).map( swiftCode -> swiftCodeMapper.entityAsMainToDTO(swiftCode, swiftCode.getIsHeadquarter()));

    }

    /**
     * Deletes Swift code and its connections from database if found
     * 
     * @param swift code, to look in database
     * @throws SwiftCodeDoesntExistException if not found
     */
    public void deleteBySwiftCode(String swift) {

        SwiftCode swiftCode = swiftCodeRepository.findById(swift).orElseThrow(SwiftCodeDoesntExistException::new);

        if(swiftCode.getBranches() != null) {

            swiftCode.getBranches().forEach(branch -> branch.setHeadquarter(null));

        }
        
        swiftCode.setHeadquarter(null);

        swiftCodeRepository.delete(swiftCode);

    }

    @Transactional
    private void addConnectionBetweenBranchsAndHeadquarterInDatabase() {

        List<SwiftCode> headquarters = swiftCodeRepository.findByIsHeadquarter(true);
        List<SwiftCode> branches = swiftCodeRepository.findByIsHeadquarter(false);

        Map<String, SwiftCode> headquarterMap = headquarters.stream()
            .collect(Collectors.toMap(
                hq -> hq.getSwiftCode().substring(0, hq.getSwiftCode().length() - 3),
                hq -> hq
            ));

        branches.forEach( branch -> {

            branch.setHeadquarter(headquarterMap.getOrDefault(
                branch.getSwiftCode().substring(0,branch.getSwiftCode().length()-3),
                null
            ));          

        });


    }

    /**
     * Validates a SWIFT code for correctness and database compatibility, then adds it to database
     *  
     * The validation includes:
     * Checking if the SWIFT code has the correct length.
     * Verifying if the isHeadquarter flag correctly matches the SWIFT code format.
     * Ensuring that the associated country data does not conflict with existing database entries.
     * 
     * After validation look up for conections from other swift codes.
     *  
     * @param swiftCodeDTO the SWIFT code entity to be validated
     * @throws SwiftCodeToShortException if the SWIFT code length is not exactly 11 characters
     * @throws SwiftCodeIsHeadquarterException if the isHeadquarter flag does not match the expected SWIFT code format
     * @throws CountryConflictException if an existing database entry has a conflicting ISO2 or name
     */
    public SwiftCode addNewSwiftCode(SwiftCodeDTO swiftCodeDTO) {

        if(swiftCodeRepository.existsById(swiftCodeDTO.swiftCode())) {

            throw new SwiftCodeAlreadyExistException(swiftCodeDTO.swiftCode() + " already in database");

        }

        SwiftCode swiftCode = swiftCodeMapper.dtoToEntity(swiftCodeDTO);

        validateSingleSwiftCode(swiftCode);

        bankService.saveBank(swiftCode.getBank());
        countryService.saveCountry(swiftCode.getCountry());

        return checkForConnectionsAndAddToDatabase(swiftCode);

    }

    private void validateSingleSwiftCode(SwiftCode swiftCode) {

        checkForSwiftCodeLength(swiftCode.getSwiftCode());
        checkForIsHeadquarterAndEndingWithXXX(swiftCode.getSwiftCode(), swiftCode.getIsHeadquarter());
        validateSingleCountryISO2AndNameCombinationIsCorrectWithDB(swiftCode.getCountry().getISO2(), swiftCode.getCountry().getName());

    }

    /**
     * Checks for existing connections between headquarters and branches and saves the given SwiftCode entity to the database.
     * 
     * If the provided SwiftCode is marked as a headquarter (isHeadquarter = true), it is first saved to the database.
     * Then, all branches starting with the same prefix are retrieved and assigned to the headquarter.
     * 
     * If the provided SwiftCode is not a headquarter (isHeadquarter = false), the method searches for an existing headquarter 
     * based on the first 8 characters of the SwiftCode ending with XXX and assigns the headquarter to the SwiftCode if found.
     * 
     * @param swiftCode the SwiftCode entity to be checked and persisted
     * @return the saved SwiftCode entity with updated connections
     */
    @Transactional
    private SwiftCode checkForConnectionsAndAddToDatabase(SwiftCode swiftCode) {

        if(swiftCode.getIsHeadquarter()) {

            swiftCodeRepository.save(swiftCode);

            List<SwiftCode> branches = swiftCodeRepository.findBySwiftCodeStartingWithAndIsHeadquarterFalse(swiftCode.getSwiftCode().substring(0,swiftCode.getSwiftCode().length()-3));

            branches.forEach(branch -> {

                branch.setHeadquarter(swiftCode);

            });
        } else {

            swiftCodeRepository.findById(

                swiftCode.getSwiftCode().substring(0,swiftCode.getSwiftCode().length()-3) + "XXX"

            ).ifPresent(swiftCode::setHeadquarter);

            swiftCodeRepository.save(swiftCode);

        }

        return swiftCode;

    }

    private void checkForSwiftCodeLength(String swiftCode) {


        if(swiftCode.length() != 11) {

            throw new SwiftCodeToShortException("SwiftCode length should be 11 but is: " + swiftCode.length());

        }

    }

    private void checkForIsHeadquarterAndEndingWithXXX(String swiftCode, Boolean isHeadquarter) {

        if(isHeadquarter != swiftCode.endsWith("XXX")) {

            throw new SwiftCodeIsHeadquarterException("isHeadquarter = " + isHeadquarter + " but swiftCode is: " + swiftCode);

        }

    }

    /**
     * Validates the combination of country ISO2 code and name against the database.
     * 
     * If a different combination of the ISO2 code or country name exists in the database, an exception is thrown.
     * If no matching entry is found, a new country entity is saved.
     * 
     * @param iso2 the ISO2 country code to validate
     * @param countryName the country name to validate
     * @throws CountryConflictException if an existing entry in the database has a conflicting ISO2 or name
     */
    private void validateSingleCountryISO2AndNameCombinationIsCorrectWithDB(String iso2, String countryName) {

        countryService.getByISO2OrNameLimit1(iso2, countryName)
            .filter(existingCountry ->

                !iso2.equals(existingCountry.getISO2()) ||
                !countryName.equals(existingCountry.getName())

            )
            .ifPresent(existingCountry -> {

                throw new CountryConflictException(
                    "Your combination: " + iso2 + " - " + countryName + " isn't the same as in db: "
                    + existingCountry.getISO2() + " - " + existingCountry.getName()
                );

            }
            
        );

    }

}
