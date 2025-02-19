package pl.dawid.poradzinski.remitly.swift.swift.sql;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class SwiftCode {
    
    @Id
    private String swiftCode;

    // Every SwiftCode needs to have a corepsonding bank name
    @ManyToOne
    @JoinColumn(name = "bankName", nullable = false)
    private BankName bankName;

    // Every SwiftCode needs to have a corespodning country
    @ManyToOne
    @JoinColumn(name = "countryISO2", nullable = false)
    private Country country;

    private String address;

    private Boolean isHeadquarter;

    @OneToMany(mappedBy = "headquarter", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<SwiftCode> branches;

    @ManyToOne
    @JoinColumn(name = "headquarterSwiftCode", nullable = true)
    private SwiftCode headquarter;
}
