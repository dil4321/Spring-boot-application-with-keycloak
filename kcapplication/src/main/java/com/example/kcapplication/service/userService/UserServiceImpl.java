package com.example.kcapplication.service.userService;

import com.example.kcapplication.entity.role.Role;
import com.example.kcapplication.entity.role.RoleFunction;
import com.example.kcapplication.entity.user.User;
import com.example.kcapplication.mapper.user.UserMapper;
import com.example.kcapplication.repository.RoleFunctionRepository;
import com.example.kcapplication.repository.RoleRepository;
import com.example.kcapplication.repository.UserRepository;
import com.example.kcapplication.request.UserCreateDTO;
import com.example.kcapplication.request.UserUpdateDTO;
import com.example.kcapplication.response.UserResponseDTO;
import com.example.kcapplication.service.userService.UserService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    Keycloak currentKeycloak;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleFunctionRepository roleFunctionRepository;
    private final UserMapper userMapper;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");


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
    public ResponseEntity<Object> createUser(UserCreateDTO userCreateDTO) {
        try {
            if (!EMAIL_PATTERN.matcher(userCreateDTO.getEmail()).matches()) {
                return new ResponseEntity<>("Email Is Not Valid", HttpStatus.NOT_ACCEPTABLE);
            }
            if (userRepository.existsByUserNameAndIsDeletedFalse(userCreateDTO.getUserName())) {
                return new ResponseEntity<>("Username Already Exist", HttpStatus.CONFLICT);
            }
            currentKeycloak = KeycloakBuilder.builder()
                    .realm(realmName)
                    .username(adminUsername)
                    .password(adminPassword)
                    .serverUrl(serverUrl)
                    .grantType("client_credentials")
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();

            UserRepresentation userKeycloak = getUserRepresentation(userCreateDTO);
            Response response = currentKeycloak.realm("SBApplication").users().create(userKeycloak);
//            currentKeycloak.realms().findAll().forEach(realmRepresentation -> System.out.println(realmRepresentation.getRealm().toString()));
            if (response.getStatusInfo().getStatusCode() == 409) {
                return new ResponseEntity<>("User already Exists", HttpStatus.BAD_REQUEST);
            } else if (response.getStatusInfo().getStatusCode() == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

//                User user = userMapper.toEntity(userCreateDTO);
                Set<Role> roles = Set.copyOf(roleRepository.findAllById(userCreateDTO.getRolesIds()));
                User user = new User();
                user.setUserName(userCreateDTO.getUserName());
                user.setFirstName(userCreateDTO.getFirstName());
                user.setLastName(userCreateDTO.getLastName());
                user.setIsActive(userCreateDTO.getIsActive());
                user.setEmail(userCreateDTO.getEmail());
                user.setPassword(userCreateDTO.getPassword());
                user.setKeycloakUserId(userId);
                user.setRoles(roles);
                User savedUser = userRepository.save(user);
                for (Role role : roles) {
                    currentKeycloak.realm("SBApplication").users().get(userId).joinGroup(role.getKeycloakRoleId());
                }
                UserResponseDTO userResponseDTO = toUserResponseDTO(savedUser);
                userResponseDTO.setUserName(savedUser.getUserName());
                userResponseDTO.setKeycloakUserId(savedUser.getKeycloakUserId());
                userResponseDTO.setEmail(savedUser.getEmail());
                return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
            }
            return new ResponseEntity<>("User Creation Failed", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            currentKeycloak = null;
        }
    }

    @Override
    public ResponseEntity<Object> updateUser(UserUpdateDTO user) {
        try {
            currentKeycloak = KeycloakBuilder.builder()
                    .realm(realmName)
                    .username(adminUsername)
                    .password(adminPassword)
                    .serverUrl(serverUrl)
                    .grantType("client_credentials")
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();

            Optional<User> userOptional = userRepository.getUserByUserIdAndIsDeletedFalse(user.getUserId());
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>("No data found", HttpStatus.NOT_FOUND);
            }
            if (userRepository.existsByUserNameAndUserIdNotAndIsDeletedFalse(userOptional.get().getUserName(), user.getUserId())) {
                return new ResponseEntity<>("Username Already Exist", HttpStatus.CONFLICT);
            }
            if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                return new ResponseEntity<>("Email Is Not Valid", HttpStatus.NOT_ACCEPTABLE);
            }
            UserResource userResource = currentKeycloak.realm("SBApplication").users().get(userOptional.get().getKeycloakUserId());
            UserRepresentation userRepresentation = userResource.toRepresentation();
//            userRepresentation.setId("c0738afa-88b7-4d51-9f35-40bfe6c19d90");
            userRepresentation.setFirstName(user.getFirstName());
            userRepresentation.setLastName(user.getLastName());
            userRepresentation.setEmail(user.getEmail());
            userResource.update(userRepresentation);
            User userEntity = userOptional.get();
            List<Role> existingRoles = userEntity.getRoles().stream().toList();

            User savedUser = userRepository.save(userMapper.toUpdateEntity(userEntity, user));
            List<Role> roleList = new ArrayList<>();
            Set<Role> roleSaveList = new HashSet<>();
            for (Long id : user.getRolesIds()) {
                Role role = roleRepository.getRoleByRoleIdAndIsDeletedFalse(id);
                roleList.add(role);
                if (!userRepository.existsByUserIdAndRolesRoleId(user.getUserId(), id)) {
                    List<Role> roleList2 = roleRepository.findRolesByUsersUserId(user.getUserId());
                    roleSaveList.addAll(roleList2);
                    roleSaveList.add(role);
                }
            }
            //add user-role
            if (!roleSaveList.isEmpty()) {
                User newUser = new User();
                newUser.setUserId(user.getUserId());
                newUser.setFirstName(savedUser.getFirstName());
                newUser.setLastName(savedUser.getLastName());
                newUser.setIsActive(savedUser.getIsActive());
                newUser.setPassword(savedUser.getPassword());
                newUser.setUserName(savedUser.getUserName());
                newUser.setEmail(savedUser.getEmail());
                newUser.setKeycloakUserId(savedUser.getKeycloakUserId());
                newUser.setIsDeleted(savedUser.getIsDeleted());
                newUser.setRoles(roleSaveList);
                userRepository.save(newUser);
            }

            savedUser.setRoles(Set.copyOf(roleList));
            // get the remove roles comparing with existing roles
            Set<Role> removedRoles = existingRoles.stream().filter(role -> !roleList.contains(role)).collect(Collectors.toSet());
            for (Role role : removedRoles) {
                roleRepository.deleteByRoleIdAndUsersUserId(role.getRoleId(), user.getUserId());
            }

            // get the add roles comparing with existing roles
            Set<Role> addedRoles = roleList.stream().filter(role -> !existingRoles.contains(role)).collect(Collectors.toSet());

            for (Role role : removedRoles) {
                currentKeycloak.realm(realmName).users().get(userOptional.get().getKeycloakUserId()).leaveGroup(role.getKeycloakRoleId());
            }
            for (Role role : addedRoles) {
                currentKeycloak.realm(realmName).users().get(userOptional.get().getKeycloakUserId()).joinGroup(role.getKeycloakRoleId());
            }
            UserResponseDTO userResponseDTO = userMapper.toDTO(userEntity);
            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            currentKeycloak = null;
        }
    }

    @Override
    public ResponseEntity<Object> deleteUser(Long id) {
        try {
            if (!userRepository.existsByUserIdAndIsDeletedFalse(id))
                return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
            if (roleRepository.existsRolesByUsersUserId(id))
                return new ResponseEntity<>("User Is Mapped. Cannot Delete!", HttpStatus.NOT_ACCEPTABLE);

            User user = userRepository.findById(id).get();
            user.setIsDeleted(true);
            userRepository.save(user);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> getAllUsers() {
        try {
            List<User> userList = userRepository.getUsersByIsDeletedFalse();
            if (userList == null) return new ResponseEntity<>("Users Not Found", HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(userList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private static UserRepresentation getUserRepresentation(UserCreateDTO userCreateDTO) {
        UserRepresentation userKeycloak = new UserRepresentation();
        userKeycloak.setUsername(userCreateDTO.getUserName());
        userKeycloak.setFirstName(userCreateDTO.getFirstName());
        userKeycloak.setLastName(userCreateDTO.getLastName());
        userKeycloak.setEmail(userCreateDTO.getEmail());
        userKeycloak.setEnabled(true);
        userKeycloak.setEmailVerified(true);

        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
//        String password = RandomStringUtils.random(5, true, true);
        passwordCredentials.setValue(userCreateDTO.getPassword());

        userKeycloak.setCredentials(List.of(passwordCredentials));
        userKeycloak.setRequiredActions(Collections.emptyList());
        return userKeycloak;
    }

    private static UserResponseDTO toUserResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getUserId());
        userResponseDTO.setPassword(user.getPassword());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setEmail(userResponseDTO.getEmail());
        userResponseDTO.setKeycloakUserId(userResponseDTO.getKeycloakUserId());
        userResponseDTO.setEmail(userResponseDTO.getEmail());
        userResponseDTO.setIsActive(user.getIsActive());
        userResponseDTO.setIsDeleted(user.getIsDeleted());
        return userResponseDTO;
    }
}
