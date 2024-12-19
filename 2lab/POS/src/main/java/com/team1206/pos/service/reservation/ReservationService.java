package com.team1206.pos.service.reservation;

import com.team1206.pos.sns.SNSService;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.common.enums.UserRoles;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.service.service.Service;
import com.team1206.pos.service.service.ServiceService;
import com.team1206.pos.user.user.User;
import com.team1206.pos.user.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ReservationService {
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
        userService.verifyUserRole(employee, UserRoles.EMPLOYEE);

        Reservation reservation = new Reservation();
        mapRequestToReservation(requestDTO, reservation);
        reservation.setService(service);
        reservation.setEmployee(employee);

        Reservation savedReservation = reservationRepository.save(reservation);

        snsService.sendSms(savedReservation.getPhone(),
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
        userService.verifyUserRole(employee, UserRoles.EMPLOYEE);

        mapRequestToReservation(requestDTO, reservation);
        reservation.setService(service);
        reservation.setEmployee(employee);

        Reservation updatedReservation = reservationRepository.save(reservation);

        snsService.sendSms(updatedReservation.getPhone(),
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
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.RESERVATION, reservationId.toString()));

        try {
            String phoneNumber = reservation.getPhone();
            reservationRepository.deleteById(reservationId);
            snsService.sendSms(phoneNumber,
                    "Hey, Your reservation was successfully cancelled. Thank you for choosing us!");

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while cancelling the reservation with ID: " + reservationId, e);
        }
    }

    public List<Reservation> findReservationsByEmployeeAndDate(UUID userId, LocalDate date) {
        // Convert the LocalDate to LocalDateTime for start and end of the day
        LocalDateTime startOfDay = date.atStartOfDay();  // 00:00:00 of the given date
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();  // 00:00:00 of the next day

        // Call the repository method to fetch reservations for the given date
        List<Reservation> reservations = reservationRepository.findReservationsByEmployeeAndDate(userId, startOfDay, endOfDay);

        if (reservations == null || reservations.isEmpty()) {
            throw new ResourceNotFoundException(ResourceType.RESERVATION, "userId: " + userId + " on " + date);
        }

        return reservations;
    }


    public Reservation getReservationEntityById(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                                    .orElseThrow(() -> new ResourceNotFoundException(
                                            ResourceType.RESERVATION,
                                            reservationId.toString()
                                    ));
    }

    // Mappers
    private ReservationResponseDTO mapToResponseDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setServiceId(reservation.getService().getId());
        dto.setServiceName(reservation.getService().getName());
        dto.setEmployeeId(reservation.getEmployee().getId());
        dto.setEmployeeFullName(reservation.getEmployee().getFirstName() + " " + reservation.getEmployee().getLastName());
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
