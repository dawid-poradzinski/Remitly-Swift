package pl.dawid.poradzinski.remitly.swift.swift.mapper;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;
import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

@Component
public class SwiftCodeMapper {
    
    /*
     * Returns view of a SwiftCode with all data
     */

    public SwiftCodeDTO entityAsMainToDTO(SwiftCode swiftCode, boolean isHeadquarter) {

        return new SwiftCodeDTO (
            
            swiftCode.getAddress(),
            swiftCode.getBank().getName(),
            swiftCode.getCountry().getISO2(),
            swiftCode.getCountry().getName(),
            swiftCode.getIsHeadquarter(),
            swiftCode.getSwiftCode(),

            isHeadquarter ? mapBranchesInHeadquarter(swiftCode) : null

        );

    }

    /**
     * map branches of headquarter from entity to DTO
     * @param swiftCode Headquarter entity, which branches will be converted
     * @return {@code Empty List} if there are no branches, otherwise {@code List<SwiftCodeDTO>} of branches
     */

    private List<SwiftCodeDTO> mapBranchesInHeadquarter(SwiftCode swiftCode) {

        return swiftCode.getBranches() == null ? List.of() : swiftCode.getBranches().stream()
        .map(branch -> entityInsideOtherToDTO(branch)).sorted(Comparator.comparing(SwiftCodeDTO::swiftCode))
        .toList();

    }


    /*
     * Returns view of a SwiftCode without country name and branches
     */

    public SwiftCodeDTO entityInsideOtherToDTO(SwiftCode swiftCode) {

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
