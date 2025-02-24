package pl.dawid.poradzinski.remitly.swift.swift.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SwiftCodeDoesntExistException extends RuntimeException{
    
    public SwiftCodeDoesntExistException(String message) {
        super(message);
    }
}
