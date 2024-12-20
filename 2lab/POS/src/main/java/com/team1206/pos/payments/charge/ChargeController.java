package com.team1206.pos.payments.charge;

import com.team1206.pos.common.validation.OneOf;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@OneOf(fields = {"percent", "amount"})
public class ChargeController {
    private final ChargeService chargeService;

    public ChargeController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @GetMapping("/charges")
    @Operation(summary = "Get all charges by type")
    public ResponseEntity<Page<ChargeResponseDTO>> getChargesByType(
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "chargeType", required = false) String chargeType) {
        log.info("Received get all charges request: limit={} offset={} chargeType={}", limit, offset, chargeType);

        ResponseEntity<Page<ChargeResponseDTO>> response = chargeType != null
                ? chargeService.handleGetChargesRequest(limit, offset, () -> chargeService.getCharges(limit, offset, chargeType))
                : chargeService.handleGetChargesRequest(limit, offset, () -> chargeService.getCharges(limit, offset));

        log.debug("Returning {} to get all charges request (limit={} offset={} chargeType={})", response.getBody().stream().toList(), limit, offset, chargeType);
        return response;
    }

    @PostMapping("/charges")
    @Operation(summary = "Create a new charge")
    public ResponseEntity<ChargeResponseDTO> createCharge(@Valid @RequestBody ChargeRequestDTO request) {
        log.info("Received create charge request: {}", request);

        ChargeResponseDTO response = chargeService.createCharge(request);

        log.debug("Returning {} to create charge request", response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/charges/{chargeId}")
    @Operation(summary = "Retrieve charge by ID")
    public ResponseEntity<ChargeResponseDTO> getChargeById(@PathVariable UUID chargeId) {
        log.info("Received get charge request: chargeId={}", chargeId);

        ChargeResponseDTO response = chargeService.getChargeById(chargeId);

        log.debug("Returning {} to get charge request (chargeId={})", response, chargeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/charges/{chargeId}")
    @Operation(summary = "Update charge by ID")
    public ResponseEntity<ChargeResponseDTO> updateCharge(
            @PathVariable UUID chargeId,
            @Valid @RequestBody ChargeRequestDTO request) {
        log.info("Received update charge request: chargeId={} {}", chargeId, request);

        ChargeResponseDTO response = chargeService.updateCharge(chargeId, request);

        log.debug("Returning {} to update charge request (chargeId={})", response, chargeId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/charges/{chargeId}")
    @Operation(summary = "Deactivate charge by ID")
    public ResponseEntity<Void> deactivateCharge(@PathVariable UUID chargeId) {
        log.info("Received deactivate charge request: chargeId={}", chargeId);

        chargeService.deactivateCharge(chargeId);

        log.debug("Returning nothing to deactivate charge request (chargeId={})", chargeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/charges/{chargeId}/reactivate")
    @Operation(summary = "Reactivate charge by ID")
    public ResponseEntity<ChargeResponseDTO> reactivateCharge(@PathVariable UUID chargeId) {
        log.info("Received reactivate charge request: chargeId={}", chargeId);

        ChargeResponseDTO response = chargeService.reactivateCharge(chargeId);

        log.debug("Returning {} to reactivate charge request (chargeId={})", response, chargeId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/products/{productId}/charges")
    @Operation(summary = "Get a product's charges")
    public ResponseEntity<List<ChargeResponseDTO>> getChargesByProduct(@PathVariable UUID productId) {
        log.info("Received get product charges request: productId={}", productId);

        List<ChargeResponseDTO> response = chargeService.getChargesOfProduct(productId);

        log.debug("Returning {} to get product charges request (productId={})", response, productId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/{productId}/charges/{chargeId}")
    @Operation(summary = "Add charge to product")
    public ResponseEntity<Void> addChargeToProduct(@PathVariable UUID productId, @PathVariable UUID chargeId) {
        log.info("Received add product charge request: productId={} chargeId={}", productId, chargeId);

        chargeService.addChargeToProduct(chargeId, productId);

        log.debug("Returning 200 OK to add product charge request (productId={} chargeId={})", productId, chargeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("products/{productId}/charges/{chargeId}")
    @Operation(summary = "Remove charge from product")
    public ResponseEntity<Void> removeChargeFromProduct(@PathVariable UUID productId, @PathVariable UUID chargeId) {
        log.info("Received remove product charge request: productId={} chargeId={}", productId, chargeId);

        chargeService.removeChargeFromProduct(chargeId, productId);

        log.debug("Returning 204 No content to remove product charge request (productId={} chargeId={})", productId, chargeId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("services/{serviceId}/charges")
    @Operation(summary = "Get a service's charges")
    public ResponseEntity<List<ChargeResponseDTO>> getChargesByService(@PathVariable UUID serviceId) {
        log.info("Received get service charges request: serviceId={}", serviceId);

        List<ChargeResponseDTO> response = chargeService.getChargesOfService(serviceId);

        log.debug("Returning {} to get service charges request (serviceId={})", response, serviceId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("services/{serviceId}/charges/{chargeId}")
    @Operation(summary = "Add charge to service")
    public ResponseEntity<Void> addChargeToService(@PathVariable UUID serviceId, @PathVariable UUID chargeId) {
        log.info("Received add service charge request: serviceId={} chargeId={}", serviceId, chargeId);

        chargeService.addChargeToService(chargeId, serviceId);

        log.debug("Returning 200 OK to add service charge request (serviceId={} chargeId={})", serviceId, chargeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("services/{serviceId}/charges/{chargeId}")
    @Operation(summary = "Remove charge from service")
    public ResponseEntity<Void> removeChargeFromService(@PathVariable UUID serviceId, @PathVariable UUID chargeId) {
        log.info("Received remove service charge request: serviceId={} chargeId={}", serviceId, chargeId);

        chargeService.removeChargeFromService(chargeId, serviceId);

        log.debug("Returning 204 No content to remove service charge request (serviceId={} chargeId={})", serviceId, chargeId);
        return ResponseEntity.noContent().build();
    }
}