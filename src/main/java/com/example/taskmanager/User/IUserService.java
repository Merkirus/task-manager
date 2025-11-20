package com.example.taskmanager.User;

import jakarta.transaction.Transactional;

import java.util.Collection;

public interface IUserService {
    @Transactional
    abstract public User addUser(User user);
    abstract public Collection<User> getUsers();
    abstract public User getUser(Long id);
    @Transactional
    abstract public User updateUser(Long id, User user);
    @Transactional
    abstract public Boolean deleteUser(Long id);
    abstract public User getCurrentUser();
}
