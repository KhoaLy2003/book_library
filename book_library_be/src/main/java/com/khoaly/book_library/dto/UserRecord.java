package com.khoaly.book_library.dto;

import org.keycloak.representations.idm.UserRepresentation;

public record UserRecord(String id, String username, String email, String firstName, String lastName) {
    public static UserRecord fromUserRepresentation(UserRepresentation userRepresentation) {
        return new UserRecord(userRepresentation.getId(), userRepresentation.getUsername(),
                userRepresentation.getEmail(), userRepresentation.getFirstName(), userRepresentation.getLastName());
    }
}
