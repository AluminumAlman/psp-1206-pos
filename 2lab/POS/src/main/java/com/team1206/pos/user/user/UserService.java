package com.team1206.pos.user.user;

import com.team1206.pos.exceptions.MerchantNotFoundException;
import com.team1206.pos.exceptions.UserNotFoundException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.merchant.MerchantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;

    public UserService(UserRepository userRepository, MerchantRepository merchantRepository) {
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {
        User user = new User();
        setUserFieldsFromRequest(user, request);

        // Fetch merchant
        Merchant merchant = merchantRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new MerchantNotFoundException(request.getMerchantId().toString()));
        user.setMerchant(merchant);

        // Save user
        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    public UserResponseDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
        return mapToResponseDTO(user);
    }

    // Helper methods
    private void setUserFieldsFromRequest(User user, UserRequestDTO request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRoles(request.getRoles());
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setMerchantId(user.getMerchant().getId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
