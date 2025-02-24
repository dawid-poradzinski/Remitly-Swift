package pl.dawid.poradzinski.remitly.swift.swift.service;

import java.util.List;
import java.util.Optional;

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

        if(swiftCode.getIsHeadquarter() && swiftCode.getBranches() != null) {

            swiftCode.getBranches().forEach(branch -> branch.setHeadquarter(null));
            swiftCodeRepository.saveAll(swiftCode.getBranches());

        }
        else if (swiftCode.getHeadquarter() != null) {

            swiftCode.getHeadquarter().getBranches().remove(swiftCode);
            swiftCodeRepository.save(swiftCode.getHeadquarter());
  
        }

        swiftCodeRepository.delete(swiftCode);

    }

}
