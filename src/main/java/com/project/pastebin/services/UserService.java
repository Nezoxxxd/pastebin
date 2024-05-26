package com.project.pastebin.services;

import com.project.pastebin.entities.User;
import com.project.pastebin.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.AccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username).orElseThrow( () -> new UsernameNotFoundException("User with email " + username + " not found"));
    }

    public void deleteUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);

        if(userOptional.isPresent()) {
            User currUser = userOptional.get();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String userEmail = userDetails.getUsername();
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

            if(isAdmin || currUser.getEmail().equals(userEmail)) {
                userRepository.deleteUserByEmail(email);
            } else {
                try {
                    throw new AccessException("You do not have permission to delete this User");
                } catch (AccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public User updateUserByEmail(String email, User user) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isPresent()) {
            User updateUser = userOptional.get();
            updateUser.setEmail(email);
            updateUser.setPassword(user.getPassword());
            updateUser.setFirstName(user.getFirstName());
            updateUser.setLastName(user.getLastName());

            userRepository.save(updateUser);
            return updateUser;
        } else {
            try {
                throw new AccessException("You do not have permission to update another Users");
            } catch (AccessException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
