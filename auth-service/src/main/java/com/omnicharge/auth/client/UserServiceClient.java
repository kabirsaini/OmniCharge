package com.omnicharge.auth.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    /**
     * Called during registration to create the user profile in user-service.
     * Auth-service owns credentials; user-service owns profile data.
     */
    @PostMapping("/api/users/internal/create")
    UserProfileResponse createUserProfile(@RequestBody CreateProfileRequest request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class CreateProfileRequest {
        private String email;
        private String fullName;
        private String mobile;
        private String role;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class UserProfileResponse {
        @com.fasterxml.jackson.annotation.JsonProperty("id")
        private Long userId;
        private String email;
        private String fullName;
        private String mobile;
    }
}
