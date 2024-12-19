package com.team1206.pos.service.service;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.common.enums.UserRoles;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.merchant.MerchantService;
import com.team1206.pos.user.user.User;
import com.team1206.pos.user.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final UserService userService;
    private final MerchantService merchantService;

    public ServiceService(ServiceRepository serviceRepository, UserService userService, MerchantService merchantService) {
        this.serviceRepository = serviceRepository;
        this.userService = userService;
        this.merchantService = merchantService;
    }

    // Get services paginated
    public Page<ServiceResponseDTO> getServices(int limit, int offset, String name, BigDecimal price, Long duration) {
        Pageable pageable = PageRequest.of(offset / limit, limit); // Convert offset and limit into Pageable

        // Fetch the filtered results
        Page<com.team1206.pos.service.service.Service> servicePage = serviceRepository.findAllWithFilters(name, price, duration, pageable); // TODO add filter by user's merchant

        return servicePage.map(this::mapToResponseDTO);
    }


    // Create Service
    public ServiceResponseDTO createService(ServiceRequestDTO requestDTO) {
        Merchant merchant = merchantService.findById(requestDTO.getMerchantId());

        com.team1206.pos.service.service.Service service = new com.team1206.pos.service.service.Service();
        SetServiceFieldsFromRequestDTO(service, requestDTO);
        service.setMerchant(merchant);

        com.team1206.pos.service.service.Service savedService = serviceRepository.save(service);
        return mapToResponseDTO(savedService);
    }

    // Update service by ID
    public ServiceResponseDTO updateService(UUID serviceId, ServiceRequestDTO requestDTO) {
        com.team1206.pos.service.service.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.SERVICE, serviceId.toString()));

        Merchant merchant = merchantService.findById(requestDTO.getMerchantId());

        SetServiceFieldsFromRequestDTO(service, requestDTO);
        service.setMerchant(merchant);

        com.team1206.pos.service.service.Service updatedService = serviceRepository.save(service);
        return mapToResponseDTO(updatedService);
    }

    // Get service by ID
    public ServiceResponseDTO getServiceById(UUID serviceId) {
        return mapToResponseDTO(getServiceEntityById(serviceId));
    }

    // Delete service by ID
    public void deleteService(UUID serviceId) {
        if (!serviceRepository.existsById(serviceId)) {
            throw new ResourceNotFoundException(ResourceType.SERVICE, serviceId.toString());
        }
        try {
            serviceRepository.deleteById(serviceId);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting the service with ID: " + serviceId, e);
        }
    }

    // Get available slots for a service on a given date
    public AvailableSlotsResponseDTO getAvailableSlots(UUID serviceId, LocalDate date) {

        // Implement logic to fetch and calculate available slots for the service
        // Return placeholder data for now
        AvailableSlotsResponseDTO responseDTO = new AvailableSlotsResponseDTO();
        return responseDTO;
    }


    // Service layer methods
    public com.team1206.pos.service.service.Service getServiceEntityById(UUID serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.SERVICE, serviceId.toString()));
    }

    // Mappers
    private ServiceResponseDTO mapToResponseDTO(com.team1206.pos.service.service.Service service) {
        ServiceResponseDTO dto = new ServiceResponseDTO();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setPrice(service.getPrice());
        dto.setDuration(service.getDuration());
        dto.setEmployeeFullNames(service.getEmployees().stream()
                .map(employee -> employee.getFirstName() + " " + employee.getLastName())
                .toList());
        dto.setMerchantId(service.getMerchant().getId());
        dto.setCreatedAt(service.getCreatedAt());
        return dto;
    }

    private void SetServiceFieldsFromRequestDTO(com.team1206.pos.service.service.Service service, ServiceRequestDTO requestDTO) {
        service.setName(requestDTO.getName());
        service.setPrice(requestDTO.getPrice());
        service.setDuration(requestDTO.getDuration());

        List<User> employees = userService.findAllById(requestDTO.getEmployeeIds());
        employees.forEach(employee -> {
            userService.verifyUserRole(employee, UserRoles.EMPLOYEE);
            userService.verifyLoggedInUserBelongsToMerchant(employee.getMerchant().getId(), "You do not have permission to perform this action");
        });
        
        service.setEmployees(employees);
    }

}
