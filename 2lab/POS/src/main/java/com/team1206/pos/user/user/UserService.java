package com.team1206.pos.user.user;

import com.team1206.pos.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.merchant.MerchantRepository;
import org.springframework.dao.DataIntegrityViolationException;
import com.team1206.pos.user.merchant.Merchant;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    // Create a new user (no merchant required)
    public UserResponseDTO createUser(UserRequestDTO request) {
        User user = new User();
        setUserFieldsFromRequest(user, request);
        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    // Get user by UUID
    public UserResponseDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));
        return mapToResponseDTO(user);
    }

    // Get all users
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToResponseDTO).toList();
    }


    // Update user
    public UserResponseDTO updateUser(UUID userId, UserRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));

        if (!request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(request.getEmail()) && !u.getId().equals(userId))) {
                throw new DataIntegrityViolationException("Email is already in use");
            }
        }

        setUserFieldsFromRequest(user, request);
        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    // Delete user
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));
        userRepository.delete(user);
    }

    // Assign merchant to user
    public UserResponseDTO assignMerchantToUser(UUID userId, UUID merchantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, userId.toString()));

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.MERCHANT, merchantId.toString()));

        user.setMerchant(merchant);
        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    // Get user by email
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, email));
        return mapToResponseDTO(user);
    }

    // Helper methods
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
        return dto;
    }

    public UUID getMerchantIdFromLoggedInUser() {
        // Retrieve the authenticated user's email
        String email =
                ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                                                                                                  .getAuthentication()
                                                                                                  .getPrincipal()).getUsername();

        // Fetch the user and return the merchant's ID if present
        return userRepository.findByEmail(email)
                             .map(User::getMerchant)
                             .map(Merchant::getId)
                             .orElse(null);
    }
}
