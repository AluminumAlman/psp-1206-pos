package com.team1206.pos.user.merchant;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/merchants")
public class MerchantController {
    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping
    public ResponseEntity<MerchantResponseDTO> createMerchant(@Valid @RequestBody MerchantRequestDTO request) {
        MerchantResponseDTO createdMerchant = merchantService.createMerchant(request);
        return ResponseEntity.ok(createdMerchant);
    }

    @GetMapping
    public ResponseEntity<List<MerchantResponseDTO>> getAllMerchants() {
        List<MerchantResponseDTO> merchants = merchantService.getAllMerchants();
        return ResponseEntity.ok(merchants);
    }
}
