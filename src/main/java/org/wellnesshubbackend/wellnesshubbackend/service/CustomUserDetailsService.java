package org.wellnesshubbackend.wellnesshubbackend.service;

import org.wellnesshubbackend.wellnesshubbackend.model.User;
import org.wellnesshubbackend.wellnesshubbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority(getUserType(user))
                ))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private String getUserType(User user) {
        // Détermine le type d'utilisateur en fonction de la classe concrète
        String className = user.getClass().getSimpleName();

        switch (className) {
            case "Expert":
                return "EXPERT";
            case "Employee":
                return "EMPLOYEE";
            case "HrPersonnel":
                return "HR_PERSONNEL";
            default:
                return "USER";
        }
    }
}