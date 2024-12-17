package com.team1206.pos.service.service;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/services")
public class ServiceController {
    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // GET: Fetch Services with Filters and Pagination
    @GetMapping
    @Operation(summary = "Get paged services")
    public ResponseEntity<Page<ServiceResponseDTO>> getServices(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "duration", required = false) Long duration) {
        log.info("Received get services request: limit={} offset={} name={} price={} duration={}",
                limit, offset, name, price, duration);

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be at least 0");
        }
        Page<ServiceResponseDTO> services = serviceService.getServices(limit, offset, name, price, duration);

        log.debug("Returning {} to get services request (limit={} offset={} name={} price={} duration={})", services, limit, offset, name, price, duration);
        return ResponseEntity.ok(services);
    }

    // POST: Create a New Service
    @PostMapping
    @Operation(summary = "Create a new service")
    public ResponseEntity<ServiceResponseDTO> createService(@Valid @RequestBody ServiceRequestDTO serviceRequestDTO) {
        log.info("Received create new service request: {}", serviceRequestDTO);

        ServiceResponseDTO serviceResponseDTO = serviceService.createService(serviceRequestDTO);

        log.debug("Returning {} to create new service request", serviceResponseDTO);
        // TODO: add to response the URI to created service.
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceResponseDTO);
    }

    // PUT: Update Service by ID
    @PutMapping("/{serviceId}")
    @Operation(summary = "Update service by ID")
    public ResponseEntity<ServiceResponseDTO> updateService(
            @PathVariable UUID serviceId,
            @Valid @RequestBody ServiceRequestDTO serviceRequestDTO) {
        log.info("Received update service request: serviceId={} {}", serviceId, serviceRequestDTO);

        ServiceResponseDTO serviceResponseDTO = serviceService.updateService(serviceId, serviceRequestDTO);

        log.debug("Returning {} to update service request (serviceId={})", serviceResponseDTO, serviceId);
        return ResponseEntity.ok(serviceResponseDTO);
    }

    // GET: Retrieve Service by ID
    @GetMapping("/{serviceId}")
    @Operation(summary = "Retrieve service by ID")
    public ResponseEntity<ServiceResponseDTO> getServiceById(@PathVariable UUID serviceId) {
        log.info("Received get service request: serviceId={}", serviceId);

        ServiceResponseDTO serviceResponseDTO = serviceService.getServiceById(serviceId);

        log.debug("Returning {} to get service request (serviceId={})", serviceResponseDTO, serviceId);
        return ResponseEntity.ok(serviceResponseDTO);
    }

    // DELETE: Remove Service by ID
    @DeleteMapping("/{serviceId}")
    @Operation(summary = "Delete a service by ID")
    public ResponseEntity<Void> deleteService(@PathVariable UUID serviceId) {
        log.info("Received delete service request: serviceId={}", serviceId);

        serviceService.deleteService(serviceId);

        log.debug("Returning nothing to delete service request (serviceId={})", serviceId);
        return ResponseEntity.noContent().build();
    }

    // GET: Get available reservation slots
    @GetMapping("/{serviceId}/availableSlots")
    @Operation(summary = "Get available reservation slots for given service and given day")
    public ResponseEntity<AvailableSlotsResponseDTO> getAvailableSlots(
            @PathVariable UUID serviceId,
            @RequestParam(value = "date") LocalDate date) {
        log.info("Received get available reservation slots request: serviceId={} date={}", serviceId, date);

        AvailableSlotsResponseDTO response = serviceService.getAvailableSlots(serviceId, date);

        log.debug("Returning {} to get available reservation slots request (serviceId={} date={})", response, serviceId, date);
        return ResponseEntity.ok(response);
    }
}
