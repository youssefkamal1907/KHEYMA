package com.kheyma.database;

import com.kheyma.model.User;
import com.kheyma.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createAdminIfNotExists();
        createTravelerIfNotExists();
    }

    private void createAdminIfNotExists() {
        String email = "admin@egyptrip.com";
        if (!userRepository.existsByEmail(email)) {
            User admin = new User();
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("Admin User");
            admin.setAge(30);
            admin.setPhoneNumber("0000000000");
            admin.getRoles().add(User.Role.ROLE_ADMIN);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            admin.setActive(true);
            
            userRepository.save(admin);
            logger.info("Admin account created: {}", email);
        } else {
            logger.info("Admin account already exists: {}", email);
        }
    }

    private void createTravelerIfNotExists() {
        String email = "osama.walid@gmail.com";
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("password123"));
            user.setName("Osama Walid");
            user.setAge(25);
            user.setPhoneNumber("01000000000");
            user.getRoles().add(User.Role.ROLE_USER);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setActive(true);
            
            userRepository.save(user);
            logger.info("Traveler account created: {}", email);
        } else {
            logger.info("Traveler account already exists: {}", email);
        }
    }
}
