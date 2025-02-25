package pl.dawid.poradzinski.remitly.swift.swift.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.exception.InvalidFileFormatException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.SwiftCodeDoesntExistException;
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

    public void saveSwiftCodes(List<SwiftCode> swiftCodes) {

        swiftCodeRepository.saveAll(swiftCodes);

    }

    public void saveExcelToDatabase(MultipartFile file) {

        if(excelUploadService.isValidExcelFile(file)) {

            try {
               
                // Get swiftCodes from excel

                List<SwiftCode> excelSwiftCodes = excelUploadService.mapExcelToDatabaseEntities(file.getInputStream());
                List<SwiftCode> newSwiftCodes = new ArrayList<>();

                // We need to check if iso2 and name combination isn't incorrect

                // Already existing countires map

                Map<String,String> existingCountries = countryService.getAllCountriesAsMap();
                List<Country> newCountires = new ArrayList<>();

                // List of Banks names to check and save to db

                List<Bank> banks = new ArrayList<>();
                
                for(SwiftCode swiftCode : excelSwiftCodes) {

                    Country country = swiftCode.getCountry();

                    // Check if ISO2 is already in db

                    if(existingCountries.containsKey(country.getISO2())) {

                        // If name is different, then skip swiftCode: wrong country

                        if(existingCountries.get(country.getISO2()).equals(country.getName())) {

                            //TODO save info about swiftCode, that was skipped

                            continue;

                        }

                        // If name is already in db and iso2 not, then skip swiftCode: wrong country

                    } else if (existingCountries.containsValue(country.getName())) {

                        //TODO save info about swiftCode, that was skipped

                        continue;

                    } else {

                        // If iso2 and name doesn't exist in db, then save it

                        // Add country to existing countries map

                        existingCountries.put(country.getISO2(), country.getName());

                        // add country to list, that will be saved at the end

                        newCountires.add(country);

                    }

                    // Give checking and saving bank to hibernate
                    
                    banks.add(swiftCode.getBank());

                    // Add swiftCode to list

                    newSwiftCodes.add(swiftCode);

                }

                // Save new counties and banks to db

                countryService.saveCountires(newCountires);
                bankService.saveBanks(banks);
                
                // Give checking and saving swiftCodes to hibernate

                saveSwiftCodes(newSwiftCodes);

                // Add connections between branches and headquarters of all swiftCodes in db

                addConnectionBetweenBranchAndHeadquarter();

            } catch (Exception e) {

                // TODO: handle exception
                
            }

        }
        else {

            throw new InvalidFileFormatException("Expected excel file");

        }

    }

    public Optional<SwiftCodeDTO> getBySwiftCode(String swift) {

        return swiftCodeRepository.findById(swift).map( swiftCode -> swiftCodeMapper.entityAsMainToDTO(swiftCode, swiftCode.getIsHeadquarter()));

    }

    public void deleteBySwiftCode(String swift) {

        SwiftCode swiftCode = swiftCodeRepository.findById(swift).orElseThrow(SwiftCodeDoesntExistException::new);

        // Delete relation for branches with headquarter

        if(swiftCode.getBranches() != null) {

            swiftCode.getBranches().forEach(branch -> branch.setHeadquarter(null));

        }
        
        // Delete relation with headquarter

        swiftCode.setHeadquarter(null);

        swiftCodeRepository.delete(swiftCode);

    }

    public void addConnectionBetweenBranchAndHeadquarter() {

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

        swiftCodeRepository.saveAll(branches);

    }

}
