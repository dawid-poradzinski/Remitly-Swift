package pl.dawid.poradzinski.remitly.swift.swift.sql;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Bank {
    
    @Id
    private String name;
    
    /**
     * A one-to-many relationship representing a set of SwiftCodes associated with this entity.  
     * The @JsonBackReference annotation prevents infinite recursion during default JSON serialization  
     * by marking this side of the relationship as the back reference.
     */

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bank")
    @JsonBackReference
    private Set<SwiftCode> swiftCodes;

    public Bank(String name) {
        this.name = name;
    }
    
}
