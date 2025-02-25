package pl.dawid.poradzinski.remitly.swift.swift.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;
import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

@Service
public class ExcelUploadService {
    
    /**
     * Checks if the provided file is a valid Excel file based on its MIME type.
     * 
     * Supported MIME types:
     * - application/vnd.ms-excel (for .xls files)
     * - application/vnd.openxmlformats-officedocument.spreadsheetml.sheet (for .xlsx files)
     * - application/vnd.ms-excel.sheet.macroEnabled.12 (for macro-enabled .xlsm files)
     * 
     * 
     * @param file The file to be valitadated.
     * @return {@code true} if the file is a valid Excel file, othwerwise {@code false}.
     */

    public boolean isValidExcelFile(MultipartFile file) {

        String mimeType = file.getContentType();

        if(mimeType != null && (
            mimeType.equals("application/vnd.ms-excel") ||
            mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
            mimeType.equals("application/vnd.ms-excel.sheet.macroEnabled.12")
        )) {

            return true;

        }

        return false;
    }

    public List<SwiftCode> mapExcelToDatabaseEntities(InputStream input) {

        // List of new swiftCodes

        List<SwiftCode> swiftCodes = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(input)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            StreamSupport.stream(sheet.spliterator(), false).skip(1).forEach(

                row -> {

                    Iterator<Cell> cellIterator = row.iterator();

                    Country country = new Country();
                    SwiftCode swiftCode = new SwiftCode();

                    int cellIndex = 0;

                    while(cellIterator.hasNext() && cellIndex < 7) {

                        Cell cell = cellIterator.next();

                        switch(cellIndex) {

                            case 0 -> country.setISO2(cell.getStringCellValue());
                            case 1 -> {

                                String swift = cell.getStringCellValue();

                                swiftCode.setIsHeadquarter(swift.endsWith("XXX"));

                                swiftCode.setSwiftCode(swift);

                            }
                            case 3 -> swiftCode.setBank(new Bank(cell.getStringCellValue()));
                            case 4 -> swiftCode.setAddress(cell.getStringCellValue());
                            case 6 -> country.setName(cell.getStringCellValue());
                            default -> {}

                        }

                        cellIndex++;

                    }

                    swiftCodes.add(swiftCode);

                }

            );


        } catch (IOException e) {

            e.getStackTrace();

        }

        return swiftCodes;

    }

}
