package com.analisys.gimnasio.clases_service.exception;

public class ClaseNotFoundException extends RuntimeException {
    
    public ClaseNotFoundException(Long id) {
        super("Clase no encontrada con ID: " + id);
    }
    
    public ClaseNotFoundException(String message) {
        super(message);
    }
}
