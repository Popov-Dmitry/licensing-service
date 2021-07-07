package com.github.popovdmitry.userservice.service;

import com.github.popovdmitry.userservice.dto.UserFilterDTO;
import com.github.popovdmitry.userservice.model.User;
import com.github.popovdmitry.userservice.model.UserType;
import com.github.popovdmitry.userservice.repository.UserRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public List<User> findAllByFilter(UserFilterDTO userFilterDTO) {
        if (userFilterDTO.getUserId() == null) {
            if (userFilterDTO.getUserTypeId() == null) {
                if (userFilterDTO.getUserName() == null || userFilterDTO.getUserName().isEmpty()) {
                    return new ArrayList<>();
                }
                else {
                    return userRepository.findAllByName(userFilterDTO.getUserName());
                }
            }
            else {
                if (userFilterDTO.getUserName() == null || userFilterDTO.getUserName().isEmpty()) {
                    return userRepository.findAllByUserType(UserType.values()[userFilterDTO.getUserTypeId()]);
                }
                else {
                    return userRepository.findAllByUserTypeAndName(
                            UserType.values()[userFilterDTO.getUserTypeId()],
                            userFilterDTO.getUserName());
                }
            }
        }
        else {
            if (userFilterDTO.getUserTypeId() == null) {
                if (userFilterDTO.getUserName() == null || userFilterDTO.getUserName().isEmpty()) {
                    return userRepository.findAllById(userFilterDTO.getUserId());
                }
                else {
                    return userRepository.findAllByIdAndName(userFilterDTO.getUserId(), userFilterDTO.getUserName());
                }
            }
            else {
                if (userFilterDTO.getUserName() == null || userFilterDTO.getUserName().isEmpty()) {
                    return userRepository.findAllByIdAndUserType(
                            userFilterDTO.getUserId(),
                            UserType.values()[userFilterDTO.getUserTypeId()]);
                }
                else {
                    return userRepository.findAllByIdAndUserTypeAndName(
                            userFilterDTO.getUserId(),
                            UserType.values()[userFilterDTO.getUserTypeId()],
                            userFilterDTO.getUserName());
                }
            }
        }
    }
}
