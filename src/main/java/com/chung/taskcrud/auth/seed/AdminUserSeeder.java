package com.chung.taskcrud.auth.seed;

import com.chung.taskcrud.auth.entity.Role;
import com.chung.taskcrud.auth.entity.User;
import com.chung.taskcrud.auth.repository.RoleRepository;
import com.chung.taskcrud.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String email = "admin@gmail.com";

        if(userRepository.existsByEmail(email)) return;

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow( () -> new IllegalStateException("Role ADMIN not found"));

        User admin = User.builder()
                .email(email)
                .password(passwordEncoder.encode("admin123"))
                .fullName("Admin")
                .enabled(true)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);
    }
}
