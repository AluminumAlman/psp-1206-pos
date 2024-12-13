package com.team1206.pos.inventory.productVariation;

public class ProductVariationService {
    private final ProductVariationRepository productVariationRepository;
    public ProductVariationService(ProductVariationRepository productVariationRepository) {
        this.productVariationRepository = productVariationRepository;
    }
}
