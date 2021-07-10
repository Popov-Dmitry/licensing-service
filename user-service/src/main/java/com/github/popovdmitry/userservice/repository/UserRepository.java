package com.github.popovdmitry.userservice.repository;

import com.github.popovdmitry.userservice.model.User;
import com.github.popovdmitry.userservice.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllById(Long id);
    List<User> findAllByUserType(UserType userType);
    List<User> findAllByName(String name);
    List<User> findAllByIdAndName(Long id, String name);
    List<User> findAllByIdAndUserType(Long id, UserType userType);
    List<User> findAllByUserTypeAndName(UserType userType, String name);
    List<User> findAllByIdAndUserTypeAndName(Long id, UserType userType, String name);
}
