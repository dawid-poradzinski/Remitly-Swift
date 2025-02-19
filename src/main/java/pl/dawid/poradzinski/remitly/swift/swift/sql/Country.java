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
    private String countryISO2;

    private String countryName;

    /**
     * A one-to-many relationship representing a set of SwiftCodes associated with this entity.  
     * No any additional addnotation here, serialization will be handled with DTOs
     * This approach ensures control over which fields are serialized and avoids infinite recursion in case of circular dependencies.
     */

    @OneToMany(mappedBy = "country")
    private Set<SwiftCode> swiftCodes;

    public void setCountryName(String name) {
        this.countryName = countryName != null ? countryName.toUpperCase() : null;
    }

    public void setCountryISO2(String countryiso2) {
        this.countryISO2 = countryISO2 != null ? countryISO2.toUpperCase() : null;
    }
}
