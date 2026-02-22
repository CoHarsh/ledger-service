package org.payment.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public UserResponse register(RegisterUserRequest req) {

        if (userRepository.findByEmail(req.email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        String username = generateUsername(req.firstName, req.lastName);

        User user = new User();
        user.userId = UUID.randomUUID();
        user.username = username;
        user.email = req.email;
        user.passwordHash = BCrypt.hashpw(req.password, BCrypt.gensalt());
        user.createdAt = Instant.now();

        userRepository.persist(user);

        return toResponse(user);
    }

    public UserResponse getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return toResponse(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        userRepository.delete("userId", id);
    }

    private String generateUsername(String firstName, String lastName) {
        String base = (firstName + "." + lastName).toLowerCase();

        int suffix = 1;
        String username = base;

        while (userRepository.findByUsername(username).isPresent()) {
            username = base + suffix;
            suffix++;
        }

        return username;
    }

    private UserResponse toResponse(User user) {
        UserResponse res = new UserResponse();
        res.userId = user.userId;
        res.username = user.username;
        res.email = user.email;
        return res;
    }
}