package pl.dawid.poradzinski.remitly.swift.swift.mapper;

import org.springframework.stereotype.Component;

import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;
import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

@Component
public class SwiftCodeMapper {
    
    /*
     * Returns view of a SwiftCode with all data to serialize
     */
    public SwiftCodeDTO entityHeadquarterToDTO(SwiftCode swiftCode) {

        return new SwiftCodeDTO (
            
            swiftCode.getAddress(),
            swiftCode.getBank().getName(),
            swiftCode.getCountry().getISO2(),
            swiftCode.getCountry().getName(),
            swiftCode.getIsHeadquarter(),
            swiftCode.getSwiftCode(),

            swiftCode.getBranches().stream()
            .map(branch -> entityBranchToDTO(branch))
            .toList()

        );
    }

    /*
     * Returns view of a SwiftCode without country name and branches to serialize
     */
    public SwiftCodeDTO entityBranchToDTO(SwiftCode swiftCode) {

        return new SwiftCodeDTO (

            swiftCode.getAddress(),
            swiftCode.getBank().getName(),
            swiftCode.getCountry().getISO2(),
            swiftCode.getIsHeadquarter(),
            swiftCode.getSwiftCode()

        );
    }

    /*
     * Return SwiftCode entity from SwiftCode dto
     */

    public SwiftCode dtoToEntity(SwiftCodeDTO dto) {

        SwiftCode swiftCode = new SwiftCode();

        swiftCode.setAddress(dto.address());
        swiftCode.setBank(new Bank(dto.bankName()));

        Country country = new Country();
        
        country.setISO2(dto.countryISO2());
        country.setName(dto.countryName());
        
        swiftCode.setCountry(country);
        swiftCode.setIsHeadquarter(dto.isHeadquarter());
        swiftCode.setSwiftCode(dto.swiftCode());

        return swiftCode;
    }
    
}
