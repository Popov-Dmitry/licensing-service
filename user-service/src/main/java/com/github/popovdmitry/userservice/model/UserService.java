package com.github.popovdmitry.userservice.model;

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

    public void updateUser(User user) throws NotFoundException {
        if (userRepository.findById(user.getId()).isPresent()) {
            userRepository.save(user);
            return;
        }
        throw new NotFoundException(String.format("User with id %d is not found", user.getId()));
    }

    public void deleteUser(User user) throws NotFoundException {
        if (userRepository.findById(user.getId()).isPresent()) {
            userRepository.delete(user);
            return;
        }
        throw new NotFoundException(String.format("User with id %d is not found", user.getId()));
    }
}
