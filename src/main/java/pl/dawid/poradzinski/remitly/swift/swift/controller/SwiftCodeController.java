package pl.dawid.poradzinski.remitly.swift.swift.controller;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pl.dawid.poradzinski.remitly.swift.swift.dto.CountryDTO;
import pl.dawid.poradzinski.remitly.swift.swift.exception.InvalidFileFormatException;
import pl.dawid.poradzinski.remitly.swift.swift.service.CountryService;
import pl.dawid.poradzinski.remitly.swift.swift.service.SwiftCodeService;

@RestController
@RequestMapping("/v1/swift-codes")
@RequiredArgsConstructor
public class SwiftCodeController {
    
    private final SwiftCodeService swiftCodeService;
    private final CountryService countryService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadSwiftCodesFromExcel(@RequestParam("file") MultipartFile file) {

        try {
            
            swiftCodeService.saveExcelToDatabase(file);

            return ResponseEntity.status(HttpStatus.OK).body("SwiftCodes added succesfully");

        } catch (InvalidFileFormatException e) {
            
            return ResponseEntity.badRequest().body("Invalid file format: " + e.getMessage());

        } catch (Exception e) { 

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occured while processing the file: " + e.getMessage());

        }

    }

    @GetMapping("/country/{countryISO2Code}")
    public ResponseEntity<CountryDTO> returnAllSwiftCodesForSpecificCountry(@PathVariable String countryISO2Code) {

        return countryService.getAllDataByISO2(countryISO2Code).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());    

    }

}
