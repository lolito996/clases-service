package com.analisys.gimnasio.clases_service.client;

import com.analisys.gimnasio.clases_service.dto.TrainerResponseDTO;
import com.analisys.gimnasio.clases_service.exception.TrainerServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class TrainerServiceClient {

    private static final Logger log = LoggerFactory.getLogger(TrainerServiceClient.class);

    private final RestTemplate restTemplate;
    private final String trainerServiceUrl;

    public TrainerServiceClient(
            RestTemplate restTemplate,
            @Value("${trainer.service.url}") String trainerServiceUrl) {
        this.restTemplate = restTemplate;
        this.trainerServiceUrl = trainerServiceUrl;
    }

    // obtener un entrenador por ID
    public TrainerResponseDTO getTrainerById(Long trainerId) {
        String url = trainerServiceUrl + "/api/entrenadores/" + trainerId;
        log.info("Consultando entrenador: {}", url);

        try {
            ResponseEntity<TrainerResponseDTO> response = restTemplate.getForEntity(
                    url, TrainerResponseDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new TrainerServiceException("Entrenador no encontrado con ID: " + trainerId);
        } catch (RestClientException e) {
            log.error("Error al comunicarse con el servicio de entrenadores: {}", e.getMessage());
            throw new TrainerServiceException("Error al comunicarse con el servicio de entrenadores", e);
        }
    }

    // obtener todos los entrenadores
    public List<TrainerResponseDTO> getAllTrainers() {
        String url = trainerServiceUrl + "/api/entrenadores";
        log.info("Obteniendo todos los entrenadores: {}", url);

        try {
            ResponseEntity<TrainerResponseDTO[]> response = restTemplate.getForEntity(
                    url, TrainerResponseDTO[].class);
            return Arrays.asList(response.getBody());
        } catch (RestClientException e) {
            log.error("Error al obtener entrenadores: {}", e.getMessage());
            throw new TrainerServiceException("Error al comunicarse con el servicio de entrenadores", e);
        }
    }

    // obtener entrenadores por especialidad (filtrado del lado del cliente)
    public List<TrainerResponseDTO> getTrainersBySpecialty(String especialidad) {
        // Filtramos del lado del cliente ya que el servicio base no tiene este endpoint
        return getAllTrainers().stream()
                .filter(t -> t.getEspecialidad() != null && 
                        t.getEspecialidad().toLowerCase().contains(especialidad.toLowerCase()))
                .toList();
    }

    // verificar si el servicio de entrenadores está disponible
    public boolean isServiceAvailable() {
        try {
            restTemplate.getForEntity(trainerServiceUrl + "/api/entrenadores", Object.class);
            return true;
        } catch (RestClientException e) {
            log.warn("Servicio de entrenadores no disponible: {}", e.getMessage());
            return false;
        }
    }
}
