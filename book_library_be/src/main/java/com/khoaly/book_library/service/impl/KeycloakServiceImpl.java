package com.khoaly.book_library.service.impl;

import com.khoaly.book_library.dto.UserRecord;
import com.khoaly.book_library.dto.UserRegistrationRecord;
import com.khoaly.book_library.exception.NotFoundException;
import com.khoaly.book_library.service.KeycloakService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord) {
        RealmResource realmResource = keycloak.realm(realm);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRegistrationRecord.password());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(userRegistrationRecord.username());
        userRepresentation.setEmail(userRegistrationRecord.email());
        userRepresentation.setFirstName(userRegistrationRecord.firstName());
        userRepresentation.setLastName(userRegistrationRecord.lastName());
        userRepresentation.setEmailVerified(false);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        Response response = realmResource.users().create(userRepresentation);

        if (Objects.equals(201, response.getStatus())) {
            //Assign role
            String userId = CreatedResponseUtil.getCreatedId(response);
            UserResource userResource = realmResource.users().get(userId);
            RolesResource rolesResource = keycloak.realm(realm).roles();
            RoleRepresentation roleRepresentation = rolesResource.get("librarian").toRepresentation();
            userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
        }

        return userRegistrationRecord;
    }

    @Override
    public List<UserRecord> getUsers() {
        return keycloak.realm(realm).users().list().stream()
                .map(UserRecord::fromUserRepresentation)
                .toList();
    }

    @Override
    public UserRecord getUser(String id) {
        try {
            UserRepresentation userRepresentation = keycloak.realm(realm).users().get(id).toRepresentation();
            return UserRecord.fromUserRepresentation(userRepresentation);
        } catch (jakarta.ws.rs.NotFoundException exception) {
            throw new NotFoundException(exception.getMessage());
        }
    }
}
