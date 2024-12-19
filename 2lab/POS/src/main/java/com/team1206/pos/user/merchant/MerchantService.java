package com.team1206.pos.user.merchant;

import com.team1206.pos.common.dto.WorkHoursDTO;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.service.schedule.Schedule;
import com.team1206.pos.service.schedule.ScheduleService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MerchantService {
    private final MerchantRepository merchantRepository;
    private final ScheduleService scheduleService;

    public MerchantService(MerchantRepository merchantRepository, ScheduleService scheduleService) {
        this.merchantRepository = merchantRepository;
        this.scheduleService = scheduleService;
    }

    // Create a new merchant
    public MerchantResponseDTO createMerchant(MerchantRequestDTO request) {
        Merchant merchant = new Merchant();
        setMerchantFieldsFromRequestDTO(merchant, request);

        Merchant savedMerchant = merchantRepository.save(merchant);
        return mapToResponseDTO(savedMerchant);
    }

    // Get all merchants
    public List<MerchantResponseDTO> getAllMerchants() {
        return merchantRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Retrieve merchant by ID
    public MerchantResponseDTO getMerchantById(UUID merchantId) {
        return merchantRepository.findById(merchantId)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.MERCHANT, merchantId.toString()));
    }

    // Update merchant by ID
    @Transactional
    public MerchantResponseDTO updateMerchantById(UUID merchantId, MerchantRequestDTO request) {
        Merchant retrievedMerchant = merchantRepository
                .findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.MERCHANT, merchantId.toString()));

        setMerchantFieldsFromRequestDTO(retrievedMerchant, request);

        Merchant updatedMerchant = merchantRepository.save(retrievedMerchant);
        return mapToResponseDTO(updatedMerchant);
    }

    // Delete merchant by ID
    public void deleteMerchantById(UUID merchantId) {
        if (!merchantRepository.existsById(merchantId)) {
            throw new ResourceNotFoundException(ResourceType.MERCHANT, merchantId.toString());
        }
        try {
            merchantRepository.deleteById(merchantId);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting the merchant with ID: " + merchantId, e);
        }
    }

    public Merchant findById(UUID merchantId) {
        return merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.MERCHANT, merchantId.toString()));
    }

    // Service layer

    // Get merchant entity by ID
    public Merchant getMerchantEntityById(UUID id) {
        return merchantRepository.findById(id)
                                 .orElseThrow(() -> new ResourceNotFoundException(
                                         ResourceType.MERCHANT,
                                         id.toString()
                                 ));
    }

    // Map Merchant entity to Response DTO
    private MerchantResponseDTO mapToResponseDTO(Merchant merchant) {
        MerchantResponseDTO response = new MerchantResponseDTO();
        response.setId(merchant.getId());
        response.setName(merchant.getName());
        response.setPhone(merchant.getPhone());
        response.setEmail(merchant.getEmail());
        response.setCurrency(merchant.getCurrency());
        response.setAddress(merchant.getAddress());
        response.setCity(merchant.getCity());
        response.setCountry(merchant.getCountry());
        response.setPostcode(merchant.getPostcode());

        Map<DayOfWeek, WorkHoursDTO> scheduleMap = merchant.getSchedules().stream()
                .collect(Collectors.toMap(
                        Schedule::getDayOfWeek,
                        schedule -> new WorkHoursDTO(schedule.getStartTime(), schedule.getEndTime())
                ));
        response.setSchedule(scheduleMap);

        response.setCreatedAt(merchant.getCreatedAt());
        response.setUpdatedAt(merchant.getUpdatedAt());
        return response;
    }

    // Set merchant fields
    private void setMerchantFieldsFromRequestDTO(Merchant merchant, MerchantRequestDTO requestDTO) {
        merchant.setName(requestDTO.getName());
        merchant.setPhone(requestDTO.getPhone());
        merchant.setEmail(requestDTO.getEmail());
        merchant.setCurrency(requestDTO.getCurrency());
        merchant.setAddress(requestDTO.getAddress());
        merchant.setCity(requestDTO.getCity());
        merchant.setCountry(requestDTO.getCountry());
        merchant.setPostcode(requestDTO.getPostcode());
        merchant.setSchedules(scheduleService.createScheduleEntities(requestDTO.getSchedule(), merchant));
    }
}
