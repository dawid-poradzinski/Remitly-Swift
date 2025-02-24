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
import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

@Service
@RequiredArgsConstructor
public class SwiftCodeService {
    
    private final SwiftCodeRepository swiftCodeRepository;
    private final ExcelUploadService excelUploadService;
    private final SwiftCodeMapper swiftCodeMapper;

    public void saveSwiftCodes(List<SwiftCode> swiftCodes) {

        swiftCodeRepository.saveAll(swiftCodes);

    }

    public void saveExcelToDatabase(MultipartFile file) {

        if(excelUploadService.isValidExcelFile(file)) {

            try {
                
                List<SwiftCode> swiftCodes = excelUploadService.mapExcelToDatabaseEntities(file.getInputStream());

                saveSwiftCodes(swiftCodes);
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
