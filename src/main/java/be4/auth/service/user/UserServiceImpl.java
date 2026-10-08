package be4.auth.service.user;

import be4.auth.datasource.dao.User;
import be4.auth.datasource.repository.UserRepository;
import be4.auth.service.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User get(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with this id not found"));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with this email not found"));
    }
}
