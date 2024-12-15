package com.team1206.pos.service.reservation;

import com.team1206.pos.SNS.SNSService;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.service.service.Service;
import com.team1206.pos.service.service.ServiceService;
import com.team1206.pos.user.user.User;
import com.team1206.pos.user.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ReservationService {
    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final ServiceService serviceService;
    private final SNSService snsService;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserService userService,
            ServiceService serviceService,
            SNSService snsService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.serviceService = serviceService;
        this.snsService = snsService;
    }

    // Get reservations paginated with filters
    public Page<ReservationResponseDTO> getReservations(
            int limit,
            int offset,
            String serviceName,
            String customerName,
            String customerEmail,
            String customerPhone,
            LocalDateTime appointedAt) {

        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Reservation> reservationsPage = reservationRepository.findAllWithFilters(
                serviceName, customerName, customerEmail, customerPhone, appointedAt, pageable);

        return reservationsPage.map(this::mapToResponseDTO);
    }

    // Create a reservation
    public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO) {
        Service service = serviceService.getServiceEntityById(requestDTO.getServiceId());

        User employee = userService.getUserEntityById(requestDTO.getEmployeeId());

        Reservation reservation = new Reservation();
        mapRequestToReservation(requestDTO, reservation);
        reservation.setService(service);
        reservation.setEmployee(employee);

        Reservation savedReservation = reservationRepository.save(reservation);

        snsService.sendSms("dev".equalsIgnoreCase(activeProfile) ? "+37061654765" : savedReservation.getPhone(),
                String.format("Hey, %s, Your reservation at %s for %s with %s %s is confirmed for %tF at %tR. Thank you for choosing us!",
                savedReservation.getFirstName(), service.getMerchant().getName(), service.getName(),
                employee.getFirstName(), employee.getLastName(),
                savedReservation.getAppointedAt(), savedReservation.getAppointedAt()));

        return mapToResponseDTO(savedReservation);
    }

    // Update a reservation
    public ReservationResponseDTO updateReservation(UUID reservationId, ReservationRequestDTO requestDTO) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.RESERVATION, reservationId.toString()));

        Service service = serviceService.getServiceEntityById(requestDTO.getServiceId());

        User employee = userService.getUserEntityById(requestDTO.getEmployeeId());

        mapRequestToReservation(requestDTO, reservation);
        reservation.setService(service);
        reservation.setEmployee(employee);

        Reservation updatedReservation = reservationRepository.save(reservation);

        snsService.sendSms("dev".equalsIgnoreCase(activeProfile) ? "+37061654765" : updatedReservation.getPhone(),
                String.format("Hey, %s, Your reservation at %s for %s with %s %s is confirmed for %tF at %tR. Thank you for choosing us!",
                        updatedReservation.getFirstName(), service.getMerchant().getName(), service.getName(),
                        employee.getFirstName(), employee.getLastName(),
                        updatedReservation.getAppointedAt(), updatedReservation.getAppointedAt()));

        return mapToResponseDTO(updatedReservation);
    }

    // Get a reservation by ID
    public ReservationResponseDTO getReservationById(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.RESERVATION, reservationId.toString()));
        return mapToResponseDTO(reservation);
    }

    // Cancel (delete) a reservation
    public void cancelReservation(UUID reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new ResourceNotFoundException(ResourceType.RESERVATION, reservationId.toString());
        }
        try {
            reservationRepository.deleteById(reservationId);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while cancelling the reservation with ID: " + reservationId, e);
        }
    }

    // Get available slots for a service on a given date
    public AvailableSlotsResponseDTO getAvailableSlots(LocalDate date, UUID serviceId) {
        Service service = serviceService.getServiceEntityById(serviceId);

        // Implement logic to fetch and calculate available slots for the service
        // Return placeholder data for now
        AvailableSlotsResponseDTO responseDTO = new AvailableSlotsResponseDTO();
        return responseDTO;
    }

    // Mappers
    private ReservationResponseDTO mapToResponseDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setServiceId(reservation.getService().getId());
        dto.setEmployeeId(reservation.getEmployee().getId());
        dto.setFirstName(reservation.getFirstName());
        dto.setLastName(reservation.getLastName());
        dto.setPhone(reservation.getPhone());
        dto.setAppointedAt(reservation.getAppointedAt());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());
        return dto;
    }

    private void mapRequestToReservation(ReservationRequestDTO requestDTO, Reservation reservation) {
        reservation.setFirstName(requestDTO.getFirstName());
        reservation.setLastName(requestDTO.getLastName());
        reservation.setPhone(requestDTO.getPhone());
        reservation.setAppointedAt(requestDTO.getAppointedAt());
    }
}
