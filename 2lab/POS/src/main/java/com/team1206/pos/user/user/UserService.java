package com.team1206.pos.user.user;

import com.team1206.pos.common.dto.WorkHoursDTO;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.common.enums.UserRoles;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.service.schedule.Schedule;
import com.team1206.pos.service.schedule.ScheduleService;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.merchant.MerchantRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final ScheduleService scheduleService;

    public UserService(UserRepository userRepository, MerchantRepository merchantRepository, ScheduleService scheduleService) {
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.scheduleService = scheduleService;
    }

    public UserResponseDTO createUser(UserRequestDTO request) {
        if(getMerchantIdFromLoggedInUser() == null) {
            throw new UnauthorizedActionException("Admin must be assigned to a Merchant");
        }

        User user = new User();
        setUserFieldsFromRequest(user, request);
        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    public UserResponseDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));
        return mapToResponseDTO(user);
    }

    public List<UserResponseDTO> getAllUsers(String firstname, String lastname, String email) {
        List<User> users = userRepository.findAll();

        Merchant merchant = getCurrentUser().getMerchant();
        if (merchant != null) {
            UUID merchantId = merchant.getId();
            users = users.stream().filter(u -> u.getMerchant() != null ? u.getMerchant().getId().equals(merchantId) : false).toList();
        }

        if (StringUtils.hasText(firstname)) {
            users = users.stream().filter(u -> u.getFirstName().equalsIgnoreCase(firstname)).toList();
        }

        if (StringUtils.hasText(lastname)) {
            users = users.stream().filter(u -> u.getLastName().equalsIgnoreCase(lastname)).toList();
        }

        if (StringUtils.hasText(email)) {
            users = users.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).toList();
        }

        return users.stream().map(this::mapToResponseDTO).toList();
    }

    public UserResponseDTO updateUser(UUID userId, UserUpdateRequestDTO request) {
        verifyAdminOrOwnerRole();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));

        verifySameMerchantIfOwner(targetUser);
        if (!request.getEmail().equalsIgnoreCase(targetUser.getEmail())) {
            if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(request.getEmail()) && !u.getId().equals(userId))) {
                throw new DataIntegrityViolationException("Email is already in use");
            }
        }

        setUserUpdateFieldsFromRequest(targetUser, request);
        User updatedUser = userRepository.save(targetUser);
        return mapToResponseDTO(updatedUser);
    }

    public void deleteUser(UUID userId) {
        verifyAdminOrOwnerRole();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));

        verifySameMerchantIfOwner(targetUser);
        userRepository.delete(targetUser);
    }

    public UserResponseDTO assignMerchantToUser(UUID userId, UUID merchantId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));

        if (merchantId == null) {
            // No merchant assigned
            targetUser.setMerchant(null);
        } else {
            Merchant merchant = merchantRepository.findById(merchantId)
                    .orElseThrow(() -> new ResourceNotFoundException(ResourceType.MERCHANT, merchantId.toString()));
            targetUser.setMerchant(merchant);
        }

        User updatedUser = userRepository.save(targetUser);
        return mapToResponseDTO(updatedUser);
    }

    // Switching Merchant for super-admin
    public UserResponseDTO switchMerchant(UUID newMerchantId) {
        // Get the current user
        User currentUser = getCurrentUser();

        if (!isCurrentUserRole(UserRoles.SUPER_ADMIN)) {
            throw new UnauthorizedActionException("Only super-admins can switch merchants.");
        }

        // If newMerchantId is null, it means logging out from the current merchant
        if (newMerchantId == null) {
            currentUser.setMerchant(null);
        } else {
            // Assign the new merchant
            Merchant merchant = merchantRepository.findById(newMerchantId)
                    .orElseThrow(() -> new ResourceNotFoundException(ResourceType.MERCHANT, newMerchantId.toString()));
            currentUser.setMerchant(merchant);
        }

        // Save the changes
        User updatedUser = userRepository.save(currentUser);

        return mapToResponseDTO(updatedUser);
    }

    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, email));
        return mapToResponseDTO(user);
    }

    public UserResponseDTO getCurrentUserInfo() {
        User currentUser = getCurrentUser();
        return mapToResponseDTO(currentUser);
    }

    // ========================================================================= //
    // Service layer methods
    public User getUserEntityById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));
    }

    // Get list of user refs from their IDs
    public List<User> findAllById(List<UUID> userIds) {
        List<User> employees = userRepository.findAllById(userIds);

        if (employees.size() != userIds.size()) {
            throw new ResourceNotFoundException(ResourceType.USER, "Some employee IDs were not found");
        }
        return employees;
    }

    public UUID getMerchantIdFromLoggedInUser() {
        // Get the authentication from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or unauthenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedActionException("No user is logged in.");
        }

        // Extract email from the principal
        String email = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

        // Retrieve the merchant ID from the user's email
        return userRepository.findByEmail(email)
                .map(User::getMerchant)
                .map(Merchant::getId)
                .orElseThrow(() -> new UnauthorizedActionException("User must have a Merchant assigned"));
    }


    // MAIN VALIDATION METHOD
    public void verifyLoggedInUserBelongsToMerchant(UUID merchantId, String messageIfInvalid) {
        // If User is assigned to a different Merchant or the super-admin didn't choose the Merchant yet (or regular user, which hasn't been assigned a merchant yet)
        if ((getCurrentUser().getRole() == UserRoles.SUPER_ADMIN && getCurrentUser().getMerchant() == null) || !getMerchantIdFromLoggedInUser().equals(merchantId)) {
            throw new UnauthorizedActionException(messageIfInvalid);
        }
    }

    public void verifyUserRole(User user, UserRoles targetUserRole) {
        if (!user.getRole().equals(targetUserRole)) {
            throw new UnauthorizedActionException("User role is invalid for this operation!");
        }
    }

    public User getCurrentUser() {
        String email =
                ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()).getUsername();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, email));
    }

    private UserRoles getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    public boolean isCurrentUserRole(UserRoles role) {
        return getCurrentUserRole() == role;
    }

    public void verifyAdminOrOwnerRole() {
        UserRoles currentUserRole = getCurrentUserRole();
        if (!(currentUserRole == UserRoles.SUPER_ADMIN || currentUserRole == UserRoles.MERCHANT_OWNER)) {
            throw new UnauthorizedActionException("You do not have permission to perform this action.");
        }
    }

    //Patikrina ar merchant owneris bando editint savo employee arba super_admin, kuris gali bet ka editint
    private void verifySameMerchantIfOwner(User targetUser) {
        User currentUser = getCurrentUser();
        UserRoles currentUserRole = currentUser.getRole();

        if (currentUserRole == UserRoles.MERCHANT_OWNER) {
            UUID currentMerchantId = currentUser.getMerchant() != null ? currentUser.getMerchant().getId() : null;
            UUID targetMerchantId = targetUser.getMerchant() != null ? targetUser.getMerchant().getId() : null;

            if (currentMerchantId == null || !currentMerchantId.equals(targetMerchantId)) {
                throw new UnauthorizedActionException("You do not have permission to perform this action.");
            }
        }
    }

    // ========================================================================= //
    // Mappers
    private void setUserFieldsFromRequest(User user, UserRequestDTO request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setMerchant(getCurrentUser().getMerchant());
        user.setSchedules(scheduleService.createScheduleEntities(request.getSchedule(), user));
    }

    private void setUserUpdateFieldsFromRequest(User user, UserUpdateRequestDTO request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setMerchant(getCurrentUser().getMerchant());
        user.setSchedules(scheduleService.createScheduleEntities(request.getSchedule(), user));
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setMerchantId(user.getMerchant() != null ? user.getMerchant().getId() : null);
        dto.setRole(user.getRole());

        // Only map schedule if the user is an EMPLOYEE
        if (user.getRole() == UserRoles.EMPLOYEE) {
            Map<DayOfWeek, WorkHoursDTO> scheduleMap = user.getSchedules().stream()
                    .collect(Collectors.toMap(
                            Schedule::getDayOfWeek,
                            schedule -> new WorkHoursDTO(schedule.getStartTime(), schedule.getEndTime())
                    ));
            dto.setSchedule(scheduleMap);
        }

        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
