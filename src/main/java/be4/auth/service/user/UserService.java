package be4.auth.service.user;

import be4.auth.datasource.dao.User;

import java.util.UUID;

public interface UserService {
    User save(User user);

    User get(UUID id);

    boolean existsByEmail(String email);

    User getByEmail(String email);
}
