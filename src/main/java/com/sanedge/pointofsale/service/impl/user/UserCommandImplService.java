package com.sanedge.pointofsale.service.impl.user;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.user.CreateUserRequest;
import com.sanedge.pointofsale.domain.requests.user.UpdateUserRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.user.UserResponse;
import com.sanedge.pointofsale.domain.responses.user.UserResponseDeleteAt;
import com.sanedge.pointofsale.exception.ResourceNotFoundException;
import com.sanedge.pointofsale.models.Role;
import com.sanedge.pointofsale.models.User;
import com.sanedge.pointofsale.models.UserRole;
import com.sanedge.pointofsale.repository.role.RoleQueryRepository;
import com.sanedge.pointofsale.repository.user.UserCommandRepository;
import com.sanedge.pointofsale.repository.user.UserQueryRepository;
import com.sanedge.pointofsale.repository.user_role.UserRoleRepository;
import com.sanedge.pointofsale.service.user.UserCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserCommandImplService implements UserCommandService {
    private UserQueryRepository userQueryRepository;
    private RoleQueryRepository roleQueryRepository;
    private UserRoleRepository userRoleRepository;
    private UserCommandRepository userCommandRepository;
    private PasswordEncoder passwordEncoder;
    private Validator validator;

    @Autowired
    public UserCommandImplService(
            Validator validator,
            RoleQueryRepository roleQueryRepository,
            UserQueryRepository userQueryRepository,
            UserCommandRepository userCommandRepository,
            PasswordEncoder passwordEncoder,
            UserRoleRepository userRoleRepository) {
        this.roleQueryRepository = roleQueryRepository;
        this.userQueryRepository = userQueryRepository;
        this.userCommandRepository = userCommandRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public ApiResponse<UserResponse> create(CreateUserRequest req) {
        validateRequest(req);

        Optional<User> existingUser = userQueryRepository.findByEmail(req.getEmail());
        if (existingUser.isPresent()) {
            log.error("❌ [REGISTER] Email already taken | Email: {}", req.getEmail());
            throw new IllegalArgumentException("Email already registered");
        }

        Role role = roleQueryRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        User newUser = new User();
        newUser.setUsername(req.getUsername());
        newUser.setFirstname(req.getFirstname());
        newUser.setLastname(req.getLastname());
        newUser.setEmail(req.getEmail());
        newUser.setPassword(passwordEncoder.encode(req.getPassword()));
        newUser.setRoles(Set.of(role));

        UserRole userRole = new UserRole();

        userRole.setUser(newUser);
        userRole.setRole(role);

        try {
            newUser = userCommandRepository.save(newUser);
            userRole = userRoleRepository.save(userRole);
        } catch (Exception e) {
            log.error("❌ Failed to create user", e);
            throw new ResourceNotFoundException("Failed to create user", e);
        }

        UserResponse userResponse = UserResponse.from(newUser);

        return ApiResponse.<UserResponse>builder()
                .status("success")
                .message("User registered successfully")
                .data(userResponse)
                .build();
    }

    @Override
    public ApiResponse<UserResponse> update(UpdateUserRequest req) {
        validateRequest(req);

        User user = userQueryRepository.findById(req.getId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + req.getId()));

        if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {
            Optional<User> existingUser = userQueryRepository.findByEmail(req.getEmail());
            if (existingUser.isPresent()) {
                log.error("❌ [UPDATE] Email already taken | Email: {}", req.getEmail());
                throw new IllegalArgumentException("Email already registered");
            }
            user.setEmail(req.getEmail());
        }

        if (req.getUsername() != null)
            user.setUsername(req.getUsername());
        if (req.getFirstname() != null)
            user.setFirstname(req.getFirstname());
        if (req.getLastname() != null)
            user.setLastname(req.getLastname());

        if (req.getPassword() != null) {
            if (!req.getPassword().equals(req.getConfirmPassword())) {
                log.error("❌ [UPDATE] Password and confirm password do not match for user ID {}", req.getId());
                throw new IllegalArgumentException("Password and confirm password do not match");
            }
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        Role role = roleQueryRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
        user.setRoles(Set.of(role));

        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        User updatedUser = userCommandRepository.save(user);

        log.info("✅ User updated successfully | User ID: {}", updatedUser.getUserId());

        return ApiResponse.<UserResponse>builder()
                .status("success")
                .message("✅ User updated successfully!")
                .data(UserResponse.from(updatedUser))
                .build();
    }

    @Override
    public ApiResponse<UserResponseDeleteAt> trashed(Integer userId) {
        log.info("🗑️ Trashing user id={}", userId);

        try {
            User user = userCommandRepository.trashed(userId.longValue());
            return ApiResponse.<UserResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ User trashed successfully!")
                    .data(UserResponseDeleteAt.from(user))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash user id={}", userId, e);
            throw new ResourceNotFoundException("Failed to trash user", e);
        }
    }

    @Override
    public ApiResponse<UserResponseDeleteAt> restore(Integer userId) {
        log.info("♻️ Restoring user id={}", userId);

        try {
            User user = userCommandRepository.restore(userId.longValue());
            return ApiResponse.<UserResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ User restored successfully!")
                    .data(UserResponseDeleteAt.from(user))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore user id={}", userId, e);
            throw new ResourceNotFoundException("Failed to restore user", e);
        }
    }

    @Override
    public ApiResponse<Boolean> deletePermanent(Integer userId) {
        log.info("🧨 Permanently deleting user id={}", userId);

        try {
            userCommandRepository.deletePermanent(userId.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 User permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete user id={}", userId, e);
            throw new ResourceNotFoundException("Failed to permanently delete user", e);
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed users");

        try {
            userCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All users restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all users", e);
            throw new ResourceNotFoundException("Failed to restore all users", e);
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed users");

        try {
            userCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All users permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all users", e);
            throw new ResourceNotFoundException("Failed to delete all users", e);
        }
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Validation failed: {}", sb.toString());
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }

}
