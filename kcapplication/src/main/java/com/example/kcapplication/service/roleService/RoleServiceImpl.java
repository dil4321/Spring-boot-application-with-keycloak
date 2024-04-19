package com.example.kcapplication.service.roleService;

import com.example.kcapplication.entity.function.Function;
import com.example.kcapplication.entity.role.Role;
import com.example.kcapplication.entity.role.RoleFunction;
import com.example.kcapplication.entity.user.User;
import com.example.kcapplication.mapper.role.RoleMapper;
import com.example.kcapplication.mapper.role.RoleRepresentationMapper;
import com.example.kcapplication.repository.FunctionRepository;
import com.example.kcapplication.repository.RoleFunctionRepository;
import com.example.kcapplication.repository.RoleRepository;
import com.example.kcapplication.request.RoleCreateDTO;
import com.example.kcapplication.request.RoleRepresentationDTO;
import com.example.kcapplication.request.RoleUpdateDTO;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    Keycloak currentKeycloak;
    private final RoleRepository roleRepository;
    private final RoleRepresentationMapper roleRepresentationMapper;
    private final FunctionRepository functionRepository;
    private final RoleFunctionRepository roleFunctionRepository;
    private final RoleMapper roleMapper;

    @Value("${keycloak.realm-name}")
    private String realmName;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Override
    public ResponseEntity<Object> createRole(RoleCreateDTO role) {
        try{
            if (roleRepository.existsRoleByRoleCodeAndIsDeletedFalse(role.getRoleCode()))
                return new ResponseEntity<>("Role Code Already Exist", HttpStatus.CONFLICT);
            if (roleRepository.existsByRoleNameAndIsDeletedFalse(role.getRoleName()))
                return new ResponseEntity<>("Role Name Already Exists!", HttpStatus.CONFLICT);

            currentKeycloak = KeycloakBuilder.builder()
                    .realm(realmName)
                    .username(adminUsername)
                    .password(adminPassword)
                    .serverUrl(serverUrl)
                    .grantType("client_credentials")
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();

            //map functions to function representation
            List<RoleRepresentation> functionList = new ArrayList<>();
            for(Long functionId: role.getFunctionIdList()) {
                Function function = functionRepository.findById(functionId).get();
                functionList.add( toRoleRepresentation(function) );
            }

            GroupRepresentation group = new GroupRepresentation();
            group.setName(role.getRoleName());
            //save role
            Response roleResponse = currentKeycloak.realm(realmName).groups().add(group);

            if (roleResponse.getStatus() == HttpStatus.CREATED.value()) {

                // get saved role
                List<GroupRepresentation> groupList = currentKeycloak.realm(realmName).groups().query(role.getRoleName());
                GroupRepresentation groupRep = groupList.stream().filter(a -> a.getName().equals(role.getRoleName())).findAny().get();

                currentKeycloak.realm(realmName).groups().group(groupRep.getId()).roles().realmLevel().add(functionList);

                role.setKeycloakRoleId(groupRep.getId());//group/role id
                Role roleData = roleMapper.toEntity(role);
                Role savedRole = roleRepository.save(roleData);
                for (Long id : role.getFunctionIdList()) {
                    if (!functionRepository.existsByFunctionId(id))
                        return new ResponseEntity<>("Function For Id: " + id + " Not Found", HttpStatus.NOT_FOUND);
                    Function function = functionRepository.findById(id).get();
                    RoleFunction roleFunction = new RoleFunction();
                    roleFunction.setFunction(functionRepository.findById(id).get());
                    roleFunction.setRole(savedRole);
                    roleFunction.setKeycloakRoleId(savedRole.getKeycloakRoleId());
                    roleFunction.setKeycloakFunctionId(function.getKeycloakFunctionId());
                    roleFunction.setIsActive(true);
                    roleFunctionRepository.save(roleFunction);
                }
                return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Role Creation Error", HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            currentKeycloak = null;
        }
    }

    @Override
    public ResponseEntity<Object> updateRole(RoleUpdateDTO role) {
        try{
            currentKeycloak = KeycloakBuilder.builder()
                    .realm(realmName)
                    .username(adminUsername)
                    .password(adminPassword)
                    .serverUrl(serverUrl)
                    .grantType("client_credentials")
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();

            Optional<Role> existingRoleOptional = roleRepository.findById(role.getId());
            if (existingRoleOptional.isEmpty()) {
                return new ResponseEntity<>("Role not found", HttpStatus.NOT_FOUND);
            }

            if (roleRepository.existsByRoleCodeAndIsDeletedFalseAndRoleIdNot(role.getRoleCode(), role.getId())) {
                return new ResponseEntity<>("Role Code Already Exists!", HttpStatus.CONFLICT);
            }
            if (roleRepository.existsByRoleNameAndIsDeletedFalseAndRoleIdNot(role.getRoleName(), role.getId())) {
                return new ResponseEntity<>("Role Name Already Exists!", HttpStatus.CONFLICT);
            }

            GroupRepresentation groupRepresentation = new GroupRepresentation();
            groupRepresentation.setName(role.getRoleName());
            //map functions to function representation
            List<RoleRepresentation> functionList = new ArrayList<>();
            for(Long functionId: role.getFunctionIdList()) {
                Function function = functionRepository.findById(functionId).get();
                functionList.add( toRoleRepresentation(function) );

            }

            Role roleData = roleRepository.findById(role.getId()).get();
            List<GroupRepresentation> groupById = currentKeycloak.realm(realmName).groups().query(roleData.getRoleName());
            List<GroupRepresentation> groupRep = groupById.stream().filter(group -> group.getName().contains(roleData.getRoleName())).toList();

            // save role
            currentKeycloak.realm(realmName).groups().group(groupRep.getFirst().getId()).update(groupRepresentation);
            Role roleSave = new Role();
            roleSave.setRoleId(role.getId());
            roleSave.setRoleCode(role.getRoleCode());
            roleSave.setRoleName(role.getRoleName());
            roleSave.setKeycloakRoleId(roleData.getKeycloakRoleId());
            roleRepository.save(roleSave);

            // get saved role
            List<GroupRepresentation> groups = currentKeycloak.realm(realmName).groups().query(role.getRoleName());
            GroupRepresentation group = groups.stream().filter(a -> a.getName().equals(role.getRoleName())).findAny().get();

            // update role functions
            List<RoleRepresentation> existingFunctionList = roleFunctionRepository.findByRole(roleSave)
                    .stream()
                    .map(record -> roleRepresentationMapper.toEntityRepresentation(roleRepresentationMapper.toDtoFunction(record.getFunction())))
                    .toList();

            //map functions to function representation and Save to role_function table
            List<RoleRepresentation> newFunctionList = new ArrayList<>();
            for(Long functionId: role.getFunctionIdList()) {
                Function function = functionRepository.findById(functionId).get();
                RoleRepresentation roleRep = toRoleRepresentation(function);
                newFunctionList.add(roleRep);
                RoleFunction roleFunction = new RoleFunction();
                roleFunction.setRole(roleSave);
                roleFunction.setFunction(function);
                roleFunction.setKeycloakFunctionId(function.getKeycloakFunctionId());
                roleFunction.setKeycloakRoleId(roleSave.getKeycloakRoleId());
                roleFunction.setIsActive(true);
                RoleFunction roleFunction1 = roleFunctionRepository.findByRoleRoleIdAndFunctionFunctionId(roleSave.getRoleId(), function.getFunctionId());

                if (roleFunction1 == null) {
                    roleFunctionRepository.save(roleFunction);
                }
            }

            // get remove functions comparing with existing functions
            List<RoleRepresentation> removedFunctions = existingFunctionList.stream().filter(roleD -> !newFunctionList.contains(roleD)).toList();
            // get add functions comparing with existing functions
            List<RoleRepresentation> addedFunctions = newFunctionList.stream().filter(roleD -> !existingFunctionList.contains(roleD)).toList();

            // add functions - KC
            currentKeycloak.realm(realmName).groups().group(roleSave.getKeycloakRoleId()).roles().realmLevel().add(addedFunctions);
            // remove functions - KC
            currentKeycloak.realm(realmName).groups().group(roleSave.getKeycloakRoleId()).roles().realmLevel().remove(removedFunctions);

            //remove kc functions from group
            for (RoleRepresentation removedFunction: removedFunctions){
//                String functionId = removedFunction.getId();
//                String functionName = removedFunction.getName();
//                Role roleNew = roleRepository.getRoleByKeycloakRoleIdAndIsDeletedFalse(roleSave.getKeycloakRoleId());
                RoleFunction roleFunction = roleFunctionRepository.findByKeycloakRoleIdAndKeycloakFunctionId(roleSave.getKeycloakRoleId(), removedFunction.getId());
                roleFunctionRepository.delete(roleFunction);
            }




            // response
            role.setKeycloakRoleId(group.getId());
//            role.setRoleRepresentation(currentKeycloak.realm(serviceName).groups().group(group.getId()).roles().realmLevel().listAll().stream().map(roleRepresentationMapper::toDtoRepresentation).toList());
            return new ResponseEntity<>(role, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> getAllRoles() {
        try{
            List<Role> roleList = roleRepository.getRolesByIsDeletedFalse();
            if (roleList == null) return new ResponseEntity<>("Roles Are Empty", HttpStatus.CONFLICT);
            return new ResponseEntity<>(roleList, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Object> deleteRole(Long roleId) {
        try{
            if (!roleRepository.existsRoleByRoleIdAndIsDeletedIsFalse(roleId))
                return new ResponseEntity<>("Role Not Found", HttpStatus.CONFLICT);
            if (roleFunctionRepository.existsRoleFunctionsByRoleRoleId(roleId))
                return new ResponseEntity<>("Role Is Mapped. Cannot Delete!", HttpStatus.NOT_ACCEPTABLE);
            Role role = roleRepository.findById(roleId).get();
            role.setIsDeleted(true);
            roleRepository.save(role);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public RoleRepresentation toRoleRepresentation(Function function) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setId(function.getKeycloakFunctionId());
        roleRepresentation.setName(function.getName());
        roleRepresentation.setComposite(false);
        roleRepresentation.setDescription(function.getDescription());
        return roleRepresentation;
    }
}
