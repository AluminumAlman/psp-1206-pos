package com.team1206.pos.service.service;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.common.enums.UserRoles;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.service.reservation.Reservation;
import com.team1206.pos.service.reservation.ReservationService;
import com.team1206.pos.service.schedule.Schedule;
import com.team1206.pos.service.schedule.ScheduleService;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.merchant.MerchantService;
import com.team1206.pos.user.user.User;
import com.team1206.pos.user.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final UserService userService;
    private final MerchantService merchantService;
    private final ScheduleService scheduleService;
    private final ReservationService reservationService;

    public ServiceService(ServiceRepository serviceRepository, UserService userService, MerchantService merchantService, ScheduleService scheduleService, ReservationService reservationService) {
        this.serviceRepository = serviceRepository;
        this.userService = userService;
        this.merchantService = merchantService;
        this.scheduleService = scheduleService;
        this.reservationService = reservationService;
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
    public AvailableSlotsResponseDTO getAvailableSlots(UUID serviceId, LocalDate date, UUID userId) {
        // Get the day of the week for the given date
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Fetch employee's schedule for the given day
        List<Schedule> schedules = scheduleService.getUserScheduleByDay(userId, dayOfWeek);

        // Create the response DTO
        AvailableSlotsResponseDTO responseDTO = new AvailableSlotsResponseDTO();

        // Get service duration (in minutes) from the service entity
        com.team1206.pos.service.service.Service service = getServiceEntityById(serviceId);
        Long serviceDurationInSeconds = service.getDuration();  // Service duration in seconds (as long)

        // Fetch existing reservations for the given date and employee
        List<Reservation> existingReservations = reservationService.findReservationsByEmployeeAndDate(userId, date);

        // Iterate through each schedule (employee's work time)
        for (Schedule schedule : schedules) {
            // Convert LocalTime to LocalDateTime based on the given date
            LocalDateTime scheduleStartTime = LocalDateTime.of(date, schedule.getStartTime());
            LocalDateTime scheduleEndTime = LocalDateTime.of(date, schedule.getEndTime());

            // Calculate the available slots based on the service duration
            LocalDateTime slotStartTime = scheduleStartTime;
            while (slotStartTime.plusSeconds(serviceDurationInSeconds).isBefore(scheduleEndTime)) {
                LocalDateTime slotEndTime = slotStartTime.plusSeconds(serviceDurationInSeconds);

                // Check if the slot is already occupied by an existing reservation
                boolean isSlotOccupied = existingReservations.stream().anyMatch(reservation ->
                        !slotStartTime.isBefore(reservation.getAppointedAt()) &&
                                slotEndTime.isAfter(reservation.getAppointedAt()) &&
                                slotStartTime.isBefore(reservation.getAppointedAt().plusSeconds(reservation.getService().getDuration()))
                );

                // If the slot is not occupied, add it to the available slots list
                if (!isSlotOccupied) {
                    AvailableSlotsResponseDTO.Slot slot = new AvailableSlotsResponseDTO.Slot();
                    slot.setStartTime(slotStartTime);
                    slot.setEndTime(slotEndTime);
                    responseDTO.getItems().add(slot);
                }

                // Move to the next available time slot
                slotStartTime = slotEndTime;
            }
        }

        // Return the response DTO with the available slots
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
