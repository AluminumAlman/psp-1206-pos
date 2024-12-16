package com.team1206.pos.user.user;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.common.enums.UserRoles;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.merchant.MerchantRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;

    public UserService(UserRepository userRepository, MerchantRepository merchantRepository) {
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
    }

    public UserResponseDTO createUser(UserRequestDTO request) {
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

    public UserResponseDTO updateUser(UUID userId, UserRequestDTO request) {
        verifyAdminOrOwnerRole();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));

        verifySameMerchantIfOwner(targetUser);
        if (!request.getEmail().equalsIgnoreCase(targetUser.getEmail())) {
            if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(request.getEmail()) && !u.getId().equals(userId))) {
                throw new DataIntegrityViolationException("Email is already in use");
            }
        }

        setUserFieldsFromRequest(targetUser, request);
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
        verifyAdminOrOwnerRole();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));

        verifySameMerchantIfOwner(targetUser);
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.MERCHANT, merchantId.toString()));

        targetUser.setMerchant(merchant);
        User updatedUser = userRepository.save(targetUser);
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
        String email =
                ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal()).getUsername();

        return userRepository.findByEmail(email)
                .map(User::getMerchant)
                .map(Merchant::getId)
                .orElse(null);
    }

    public void verifyUserRole(User user, UserRoles userRole) {
        if (!user.getRole().equals(userRole)) {
            throw new UnauthorizedActionException("User role is invalid for this operation!", "");
        }
    }

    private User getCurrentUser() {
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

    public void verifyAdminOrOwnerRole() {
        UserRoles currentUserRole = getCurrentUserRole();
        if (!(currentUserRole == UserRoles.SUPER_ADMIN || currentUserRole == UserRoles.MERCHANT_OWNER)) {
            throw new UnauthorizedActionException("You do not have permission to perform this action.", "");
        }
    }

    private void verifySameMerchantIfOwner(User targetUser) {
        User currentUser = getCurrentUser();
        UserRoles currentUserRole = currentUser.getRole();

        if (currentUserRole == UserRoles.MERCHANT_OWNER) {
            UUID currentMerchantId = currentUser.getMerchant() != null ? currentUser.getMerchant().getId() : null;
            UUID targetMerchantId = targetUser.getMerchant() != null ? targetUser.getMerchant().getId() : null;

            if (currentMerchantId == null || !currentMerchantId.equals(targetMerchantId)) {
                throw new UnauthorizedActionException("You do not have permission to perform this action.", "You do not have permission to modify a user from a different merchant.");
            }
        }
    }

    // Mappers
    private void setUserFieldsFromRequest(User user, UserRequestDTO request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setMerchantId(user.getMerchant() != null ? user.getMerchant().getId() : null);
        return dto;
    }
}
