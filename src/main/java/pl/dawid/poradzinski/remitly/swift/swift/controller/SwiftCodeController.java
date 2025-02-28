package pl.dawid.poradzinski.remitly.swift.swift.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pl.dawid.poradzinski.remitly.swift.swift.dto.CountryDTO;
import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.exception.InvalidFileFormatException;
import pl.dawid.poradzinski.remitly.swift.swift.exception.SwiftCodeDoesntExistException;
import pl.dawid.poradzinski.remitly.swift.swift.service.CountryService;
import pl.dawid.poradzinski.remitly.swift.swift.service.SwiftCodeService;
import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

@RestController
@RequestMapping("/v1/swift-codes")
@RequiredArgsConstructor
public class SwiftCodeController {
    
    private final SwiftCodeService swiftCodeService;
    private final CountryService countryService;

    /**
     * Handles the upload of an Excel file containing SWIFT codes and saves the data to the database.
     * 
     * @param file the uploaded Excel file containing SWIFT codes
     * @return {@code ResponseEntity<Map<String, String>>} containing a success message 
     * with the number of records added, or an error message in case of failure.
     *         
     * @throws InvalidFileFormatException if the uploaded file is not a valid Excel file
     * @throws Exception if an unexpected error occurs during processing
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String,String>> uploadSwiftCodesFromExcel(@RequestParam("file") MultipartFile file) {

        try {
            
            int size = swiftCodeService.saveExcelToDatabase(file);

            return ResponseEntity.ok(Map.of("message","Saved or updated " + size + " entities"));

        } catch (InvalidFileFormatException e) {
            
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid file format: " + e.getMessage()));

        } catch (Exception e) { 

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An error occured while processing the file: " + e.getMessage()));

        }

    }

     /**
     * Retrieves all SWIFT codes associated with a specific country.
     * 
     * @param countryISO2Code the two-letter ISO code of the country
     * @return {@code ResponseEntity<CountryDTO>} containing country details with SWIFT codes,
     * or {@code ResponseEntity.notFound()} if no data is found.
     */
    @GetMapping("/country/{countryISO2Code}")
    public ResponseEntity<CountryDTO> returnAllSwiftCodesForSpecificCountry(@PathVariable String countryISO2Code) {

        return countryService.getAllDataByISO2(countryISO2Code).map(ResponseEntity::ok).
            orElseGet(() -> ResponseEntity.notFound().build());    

    }

    /**
     * Retrieves details of a specific SWIFT code.
     * 
     * @param swiftCode the SWIFT code to look up
     * @return {@code ResponseEntity<SwiftCodeDTO>} containing SWIFT code details,
     * or {@code ResponseEntity.notFound()} if no match is found.
     */
    @GetMapping("/{swiftCode}")
    public ResponseEntity<SwiftCodeDTO> returnSwiftCodeDTOForSpecificSwift(@PathVariable String swiftCode) {

        return swiftCodeService.getBySwiftCode(swiftCode)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Deletes a specific SWIFT code from the database.
     * 
     * @param swiftCode the SWIFT code to delete
     * @return {@code ResponseEntity<Map<String,String>>} with a success message if deleted,
     * or {@code HttpStatus.NOT_FOUND} if the SWIFT code does not exist.
     */
    @DeleteMapping("/{swiftCode}")
    public ResponseEntity<Map<String,String>> deleteSwiftCodeForSpecificSwift(@PathVariable String swiftCode) {

        try {
          
            swiftCodeService.deleteBySwiftCode(swiftCode);
            return ResponseEntity.ok(Map.of("message", swiftCode + " Deleted succesfully"));
            
        } catch (SwiftCodeDoesntExistException e) {
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", swiftCode + " not found in database"));

        } 

    }

    /**
     * Adds a new SWIFT code to the database.
     * 
     * @param swiftCodeDTO the SWIFT code data to be added
     * @return {@code ResponseEntity<Map<String,String>>} with a success message containing the SWIFT code ID,
     * or {@code HttpStatus.BAD_REQUEST} if an error occurs.
     */
    @PostMapping()
    public ResponseEntity<Map<String,String>> addNewSwiftCode(@Valid @RequestBody SwiftCodeDTO swiftCodeDTO) {

        try {
            
            SwiftCode swiftCode = swiftCodeService.addNewSwiftCode(swiftCodeDTO);

            return ResponseEntity.ok(Map.of("message", "Created swiftcode id: " + swiftCode.getSwiftCode()));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
            
        }

    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {

        StringBuilder builder = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            builder.append(error.getDefaultMessage() + ". ")
        );
        return ResponseEntity.badRequest().body(Map.of("message:", builder.toString()));
    }

}
