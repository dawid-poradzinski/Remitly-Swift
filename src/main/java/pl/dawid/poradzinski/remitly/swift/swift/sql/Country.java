package pl.dawid.poradzinski.remitly.swift.swift.sql;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Country {
    
    @Id
    private String ISO2;

    private String name;

    /**
     * A one-to-many relationship representing a set of SwiftCodes associated with this entity.  
     * No any additional addnotation here, serialization will be handled with DTOs
     * This approach ensures control over which fields are serialized and avoids infinite recursion in case of circular dependencies.
     */

    @OneToMany(mappedBy = "country")
    private Set<SwiftCode> swiftCodes;

    /**
     * Sets the country ISO2 code ensuring it is stored in uppercase.
     * 
     * Country ISO2 codes must always be stored and returned as uppercase strings to maintain consistency.
     * If the provieded ISO2 code is not null, it is converted to uppercase, otherwise it remains null
     * 
     * @param ISO2 country ISO2 code to be set, which will be converted to uppercase
     */

    
    public void setISO2(String ISO2) {
        this.ISO2 = ISO2 != null ? ISO2.toUpperCase() : null;
    }

    /**
     * Sets the country name ensuring it is stored in uppercase.
     * 
     * Country names must always be stored and returned as uppercase string to maintain consistency.
     * If the provided name is not null, it is converted to uppercase, otherwise it remains null
     * 
     * @param name country name to be set, which will be converted to uppercase
     */

    public void setName(String name) {
        this.name = name != null ? name.toUpperCase() : null;
    }
    
}
