package com.team1206.pos.service.service;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

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
    public ResponseEntity<PagedModel<EntityModel<ServiceResponseDTO>>> getServices(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "duration", required = false) Long duration,
            Pageable pageable,
            PagedResourcesAssembler<ServiceResponseDTO> assembler) {

        // Validate parameters
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be at least 0");
        }

        if (limit > 100) {
            limit = 100;
        }

        // Fetch services from the service layer
        Page<ServiceResponseDTO> servicePage = serviceService.getServices(limit, offset, name, price, duration);

        // Convert the Page<ServiceResponseDTO> to a PagedModel<EntityModel<ServiceResponseDTO>>
        org.springframework.hateoas.PagedModel<EntityModel<ServiceResponseDTO>> pagedModel = assembler.toModel(servicePage);

        return ResponseEntity.ok(pagedModel);
    }


    // POST: Create a New Service
    @PostMapping
    @Operation(summary = "Create a new service")
    public ResponseEntity<ServiceResponseDTO> createService(@Valid @RequestBody ServiceRequestDTO serviceRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.createService(serviceRequestDTO));
    }

    // PUT: Update Service by ID
    @PutMapping("/{serviceId}")
    @Operation(summary = "Update service by ID")
    public ResponseEntity<ServiceResponseDTO> updateService(
            @PathVariable UUID serviceId,
            @Valid @RequestBody ServiceRequestDTO serviceRequestDTO) {
        return ResponseEntity.ok(serviceService.updateService(serviceId, serviceRequestDTO));
    }

    // GET: Retrieve Service by ID
    @GetMapping("/{serviceId}")
    @Operation(summary = "Retrieve service by ID")
    public ResponseEntity<ServiceResponseDTO> getServiceById(@PathVariable UUID serviceId) {
        return ResponseEntity.ok(serviceService.getServiceById(serviceId));
    }

    // DELETE: Remove Service by ID
    @DeleteMapping("/{serviceId}")
    @Operation(summary = "Delete a service by ID")
    public ResponseEntity<Void> deleteService(@PathVariable UUID serviceId) {
        serviceService.deleteService(serviceId);
        return ResponseEntity.noContent().build();
    }
}
