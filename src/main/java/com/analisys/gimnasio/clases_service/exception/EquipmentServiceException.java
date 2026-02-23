package com.analisys.gimnasio.clases_service.exception;

public class EquipmentServiceException extends RuntimeException {
    
    public EquipmentServiceException(String message) {
        super(message);
    }
    
    public EquipmentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
