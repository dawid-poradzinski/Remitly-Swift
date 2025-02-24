package pl.dawid.poradzinski.remitly.swift.swift.sql;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private String address;
   
    private Boolean isHeadquarter;


    
    @ManyToOne
    @JoinColumn(name = "name", nullable = false)
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "ISO2", nullable = false)
    private Country country;


    
    @OneToMany(mappedBy = "headquarter", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<SwiftCode> branches;

    @JsonIgnore
    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "headquarterSwiftCode",nullable = true)
    private SwiftCode headquarter;
    
}
