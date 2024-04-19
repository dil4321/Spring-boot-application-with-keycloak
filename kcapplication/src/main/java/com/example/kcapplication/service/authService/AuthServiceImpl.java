package com.example.kcapplication.service.authService;

import com.example.kcapplication.entity.user.User;
import com.example.kcapplication.repository.UserRepository;
import com.example.kcapplication.request.LoginDTO;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.keycloak.authorization.client.Configuration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Autowired
    UserRepository userRepository;
    Keycloak currentKeycloak;

    @Value("${keycloak.realm-name}")
    String realmName;

    @Value("${keycloak.admin-username}")
    String adminUsername;

    @Value("${keycloak.admin-password}")
    String adminPassword;

    @Value("${keycloak.client-id}")
    String clientId;

    @Value("${keycloak.client-secret}")
    String clientSecret;

    @Value("${keycloak.server-url}")
    String serverUrl;

    @Override
    public ResponseEntity<Object> login(LoginDTO loginDTO) {
        try {
            currentKeycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realmName)
                    .grantType("password")
                    .username(loginDTO.getUserName())
                    .password(loginDTO.getPassword())
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
            return new ResponseEntity<>(currentKeycloak.tokenManager().getAccessTokenString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            currentKeycloak = null;
        }
    }

    @Override
    public ResponseEntity<Object> logout(Long userId) {
        try {
            currentKeycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realmName)
                    .grantType("client_credentials")
                    .username(adminUsername)
                    .password(adminPassword)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();

            Optional<User> user = userRepository.getUserByUserIdAndIsDeletedFalse(userId);
            if (user.isEmpty())
                return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);

            // Get the realm resource
            RealmResource realmResource = currentKeycloak.realm(realmName);

            // Get the user resource
            UserResource userResource = realmResource.users().get(user.get().getKeycloakUserId());

            // Logout the user
            userResource.logout();

            return new ResponseEntity<>("Logout Successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            currentKeycloak = null;
        }
    }
    }
