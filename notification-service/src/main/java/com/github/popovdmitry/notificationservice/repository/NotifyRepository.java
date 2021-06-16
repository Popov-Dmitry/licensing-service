package com.github.popovdmitry.notificationservice.repository;

import com.github.popovdmitry.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotifyRepository extends JpaRepository<Notification, Long> {
}
