package com.github.popovdmitry.userservice.service;

import com.github.popovdmitry.userservice.model.User;
import com.github.popovdmitry.userservice.repository.UserRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findUser(Long id) throws NotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new NotFoundException(String.format("User with id %d is not found", id));
    }

    public void updateUser(Long id, User user) throws NotFoundException {
        Optional<User> originalUser = userRepository.findById(id);
        if (originalUser.isPresent()) {
            user.setId(originalUser.get().getId());
            userRepository.save(user);
            return;
        }
        throw new NotFoundException(String.format("User with id %d is not found", user.getId()));
    }

    public void deleteUser(Long id) throws NotFoundException {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return;
        }
        throw new NotFoundException(String.format("User with id %d is not found", id));
    }
}
