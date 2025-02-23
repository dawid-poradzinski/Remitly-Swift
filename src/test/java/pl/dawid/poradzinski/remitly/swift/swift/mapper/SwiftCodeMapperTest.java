package pl.dawid.poradzinski.remitly.swift.swift.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pl.dawid.poradzinski.remitly.swift.swift.dto.SwiftCodeDTO;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Bank;
import pl.dawid.poradzinski.remitly.swift.swift.sql.Country;
import pl.dawid.poradzinski.remitly.swift.swift.sql.SwiftCode;

public class SwiftCodeMapperTest {
    
    /**
     * Generates default swiftCode object with the provided paremeters.
     * 
     * This method is used to generate a SwiftCode instace for testing purposes,
     * where the parameters allow for flexible configuration.
     * 
     * @param code The SWIFT code to be assigned to the SwiftCode object. 
     * @param isHeadquarter A flag indicating wheter this SwiftCode represents a headquarter.
     * @param headquarter The associated headquarter SwiftCode (nullable).
     * @param branches A set of branches related to this SwiftCode (nullable).
     * @return {@code SwiftCode} object with the given properties set.
     */

    protected SwiftCode createTestSwiftCode(String code, boolean isHeadquarter, SwiftCode headquarter, Set<SwiftCode> branches) {

        Country country = new Country();

        country.setISO2("PL");
        country.setName("POLAND");

        SwiftCode swiftCode = new SwiftCode();

        swiftCode.setAddress("Bank address");
        swiftCode.setCountry(country);
        swiftCode.setBank(new Bank("Poland bank"));
        swiftCode.setSwiftCode(code);
        swiftCode.setIsHeadquarter(isHeadquarter);
        swiftCode.setHeadquarter(headquarter);
        swiftCode.setBranches(branches);

        return swiftCode;

    }

    private SwiftCodeMapper swiftCodeMapper;

    @BeforeEach
    void setUp() {
        
        swiftCodeMapper = new SwiftCodeMapper();

    }

    @Test
    void testMapHeadquarterWithoutBranchesToDTO() {

        // Given 
        
        SwiftCode swiftCode = createTestSwiftCode("AAABBBCCXXX", true, null, null);

        // When

        SwiftCodeDTO result = swiftCodeMapper.entityAsMainToDTO(swiftCode, true);

        // Then

        assertNotNull(result);
        assertEquals(swiftCode.getAddress(), result.address());
        assertEquals(swiftCode.getBank().getName(), result.bankName());
        assertEquals(swiftCode.getCountry().getISO2(), result.countryISO2());
        assertEquals(swiftCode.getCountry().getName(), result.countryName());
        assertEquals(swiftCode.getIsHeadquarter(), result.isHeadquarter());
        assertEquals(swiftCode.getSwiftCode(), result.swiftCode());
        assertNotNull(result.branches());
        assertEquals(0, result.branches().size());
        
    }

    @Test
    void testMapHeadquarterWithBranchesToDTO() {

        // Given
        
        SwiftCode swiftCode = createTestSwiftCode("AAABBBCCXXX", true, null, null);

        SwiftCode branch = createTestSwiftCode("AAABBBCC000", false, swiftCode, null);

        swiftCode.setBranches(Set.of(branch));

        // When

        SwiftCodeDTO result = swiftCodeMapper.entityAsMainToDTO(swiftCode, true);

        // Then

        assertNotNull(result);
        assertEquals(swiftCode.getAddress(), result.address());
        assertEquals(swiftCode.getBank().getName(), result.bankName());
        assertEquals(swiftCode.getCountry().getISO2(), result.countryISO2());
        assertEquals(swiftCode.getCountry().getName(), result.countryName());
        assertEquals(swiftCode.getIsHeadquarter(), result.isHeadquarter());
        assertEquals(swiftCode.getSwiftCode(), result.swiftCode());
        assertNotNull(result.branches());
        assertEquals(1, result.branches().size());
        
    }

    @Test
    void testMapBranchInsideObjectToDTO() {
     
        // Given

        SwiftCode swiftCode = createTestSwiftCode("AAABBBCCXXX", true, null, null);

        SwiftCode branch = createTestSwiftCode("AAABBBCC000", false, swiftCode, null);

        swiftCode.setBranches(Set.of(branch));

        // When

        SwiftCodeDTO result = swiftCodeMapper.entityAsMainToDTO(swiftCode, true);

        // Then

        assertNotNull(result);
        assertNotNull(result.branches());

        result = result.branches().get(0);

        assertEquals(branch.getAddress(), result.address());
        assertEquals(branch.getBank().getName(), result.bankName());
        assertEquals(branch.getCountry().getISO2(), result.countryISO2());
        assertNull(result.countryName());
        assertEquals(branch.getIsHeadquarter(), result.isHeadquarter());
        assertEquals(branch.getSwiftCode(), result.swiftCode());
        assertNull(result.branches());

    }


    @Test
    void testMapBranchAsMainToDTO() {

        // Given headquarterSwiftCode
        
        SwiftCode swiftCode = createTestSwiftCode("AAABBBCC000", false, null, null);

        // When

        SwiftCodeDTO result = swiftCodeMapper.entityAsMainToDTO(swiftCode, false);

        // Then

        assertNotNull(result);
        assertEquals(swiftCode.getAddress(), result.address());
        assertEquals(swiftCode.getBank().getName(), result.bankName());
        assertEquals(swiftCode.getCountry().getISO2(), result.countryISO2());
        assertEquals(swiftCode.getCountry().getName(), result.countryName());
        assertEquals(swiftCode.getIsHeadquarter(), result.isHeadquarter());
        assertEquals(swiftCode.getSwiftCode(), result.swiftCode());
        assertNull(result.branches());
        
    }

}
