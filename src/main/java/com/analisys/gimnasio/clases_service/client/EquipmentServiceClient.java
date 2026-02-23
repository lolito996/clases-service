package com.analisys.gimnasio.clases_service.client;

import com.analisys.gimnasio.clases_service.dto.EquipmentResponseDTO;
import com.analisys.gimnasio.clases_service.dto.UseEquipmentRequestDTO;
import com.analisys.gimnasio.clases_service.exception.EquipmentServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Cliente HTTP para comunicarse con el servicio de equipos (gym-equipment-service).
 * Encapsula toda la lógica de comunicación HTTP con el microservicio de equipos.
 */
@Component
public class EquipmentServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentServiceClient.class);

    private final RestTemplate restTemplate;
    private final String equipmentServiceUrl;

    public EquipmentServiceClient(RestTemplate restTemplate,
                                   @Value("${equipment.service.url}") String equipmentServiceUrl) {
        this.restTemplate = restTemplate;
        this.equipmentServiceUrl = equipmentServiceUrl;
    }

    // obtener todos los equipos disponibles
    public List<EquipmentResponseDTO> getAllEquipment() {
        String url = equipmentServiceUrl + "/api/equipment";
        logger.info("Obteniendo todos los equipos desde: {}", url);
        
        try {
            ResponseEntity<EquipmentResponseDTO[]> response = restTemplate.getForEntity(
                url, 
                EquipmentResponseDTO[].class
            );
            
            if (response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
            return Collections.emptyList();
            
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con el servicio de equipos: {}", e.getMessage());
            throw new EquipmentServiceException("No se puede conectar con el servicio de equipos", e);
        } catch (HttpServerErrorException e) {
            logger.error("Error del servidor de equipos: {}", e.getMessage());
            throw new EquipmentServiceException("Error en el servicio de equipos: " + e.getStatusCode(), e);
        }
    }

    // obtener equipo por ID
    public Optional<EquipmentResponseDTO> getEquipmentById(Long equipmentId) {
        String url = equipmentServiceUrl + "/api/equipment/" + equipmentId;
        logger.info("Obteniendo equipo {} desde: {}", equipmentId, url);
        
        try {
            ResponseEntity<EquipmentResponseDTO> response = restTemplate.getForEntity(
                url, 
                EquipmentResponseDTO.class
            );
            return Optional.ofNullable(response.getBody());
            
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Equipo {} no encontrado", equipmentId);
            return Optional.empty();
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión con el servicio de equipos: {}", e.getMessage());
            throw new EquipmentServiceException("No se puede conectar con el servicio de equipos", e);
        } catch (HttpServerErrorException e) {
            logger.error("Error del servidor de equipos: {}", e.getMessage());
            throw new EquipmentServiceException("Error en el servicio de equipos: " + e.getStatusCode(), e);
        }
    }

    // usar/reservar un equipo (disminuye cantidad disponible)
    public EquipmentResponseDTO useEquipment(Long equipmentId, int quantity) {
        String url = equipmentServiceUrl + "/api/equipment/" + equipmentId + "/use";
        logger.info("Reservando {} unidades del equipo {} en: {}", quantity, equipmentId, url);
        
        try {
            UseEquipmentRequestDTO request = new UseEquipmentRequestDTO(quantity);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UseEquipmentRequestDTO> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<EquipmentResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                EquipmentResponseDTO.class
            );
            
            logger.info("Equipo {} reservado exitosamente", equipmentId);
            return response.getBody();
            
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Equipo {} no encontrado", equipmentId);
            throw new EquipmentServiceException("Equipo no encontrado con ID: " + equipmentId);
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión: {}", e.getMessage());
            throw new EquipmentServiceException("No se puede conectar con el servicio de equipos", e);
        } catch (HttpServerErrorException e) {
            logger.error("Error del servidor: {}", e.getMessage());
            throw new EquipmentServiceException("Error en el servicio de equipos", e);
        }
    }

    // libera/devuelve un equipo (aumenta cantidad disponible)
    public EquipmentResponseDTO releaseEquipment(Long equipmentId, int quantity) {
        String url = equipmentServiceUrl + "/api/equipment/" + equipmentId + "/release";
        logger.info("Liberando {} unidades del equipo {} en: {}", quantity, equipmentId, url);
        
        try {
            UseEquipmentRequestDTO request = new UseEquipmentRequestDTO(quantity);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UseEquipmentRequestDTO> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<EquipmentResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                EquipmentResponseDTO.class
            );
            
            logger.info("Equipo {} liberado exitosamente", equipmentId);
            return response.getBody();
            
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Equipo {} no encontrado", equipmentId);
            throw new EquipmentServiceException("Equipo no encontrado con ID: " + equipmentId);
        } catch (ResourceAccessException e) {
            logger.error("Error de conexión: {}", e.getMessage());
            throw new EquipmentServiceException("No se puede conectar con el servicio de equipos", e);
        } catch (HttpServerErrorException e) {
            logger.error("Error del servidor: {}", e.getMessage());
            throw new EquipmentServiceException("Error en el servicio de equipos", e);
        }
    }

    // verifica cantidad disponible de un equipo
    public boolean isEquipmentAvailable(Long equipmentId, int quantityNeeded) {
        return getEquipmentById(equipmentId)
            .map(equipment -> 
                "DISPONIBLE".equals(equipment.getEstado()) && 
                equipment.getCantidadDisponible() >= quantityNeeded)
            .orElse(false);
    }   
}
