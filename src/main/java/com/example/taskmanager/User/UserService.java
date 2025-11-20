package com.example.taskmanager.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public User addUser(User user) {
        Long _id = user.getId();
        user.setId(_id);
        return userRepository.save(user);
    }

    @Override
    public Collection<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        if (!Objects.equals(id, user.getId())) {
            // throw
        }

        User old_user =  userRepository.findById(id).get();

        old_user.setName(user.getName());
        old_user.setEmail(user.getEmail());
        old_user.setPassword(user.getPassword());
        old_user.setRole(user.getRole());
        old_user.setCreatedAt(user.getCreatedAt());
        old_user.setUpdatedAt(user.getUpdatedAt());

        userRepository.save(old_user);
        return old_user;
    }

    @Override
    @Transactional
    public Boolean deleteUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            // throw
        }

        userRepository.deleteById(id);
        return userRepository.findById(id).isEmpty();
    }

    public User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
