package com.github.popovdmitry.notificationservice.service;

import com.github.popovdmitry.notificationservice.dto.LicenseInfoDTO;
import com.github.popovdmitry.notificationservice.dto.UserInfoDTO;
import com.github.popovdmitry.notificationservice.model.Notification;
import com.github.popovdmitry.notificationservice.repository.NotifyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotifyRepository notifyRepository;
    private final Map<Long, Notification> notificationMap = new HashMap<>();

    public Long saveLicenseInfo(LicenseInfoDTO licenseInfoDTO) {
        Notification notification = new Notification();
        notification.setProductName(licenseInfoDTO.getProductName());
        notification.setEndDate(licenseInfoDTO.getEndDate());
        notificationMap.put(notification.getId(), notification);
        return notification.getId();
    }

    public void saveUserInfo(String id, UserInfoDTO userInfoDTO) {
        Notification notification = notificationMap.get(Long.parseLong(id));
        notification.setUserName(userInfoDTO.getUserName());
        notification.setEmail(userInfoDTO.getEmail());
        notificationMap.put(Long.parseLong(id), notification);
    }

    @Scheduled(cron = "0 0 4 * * ?")
    private void saveNotifications() {
        notifyRepository.deleteAll();
        notificationMap.forEach((k, v) -> notifyRepository.save(v));
    }

}
