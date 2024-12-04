package com.khoaly.book_library.service;

import com.khoaly.book_library.dto.UserRecord;
import com.khoaly.book_library.dto.UserRegistrationRecord;

import java.util.List;

public interface KeycloakService {
    UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord);
    List<UserRecord> getUsers();
    UserRecord getUser(String id);
}
