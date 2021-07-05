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
    private final EmailService emailService;
    private final Map<Long, Notification> notificationMap = new HashMap<>();

    private long notificationId = -1;


    public long saveLicenseInfo(LicenseInfoDTO licenseInfoDTO) {
        notificationId++;
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setProductName(licenseInfoDTO.getProductName());
        notification.setEndDate(licenseInfoDTO.getEndDate());
        notificationMap.put(notification.getId(), notification);
        return notificationId;
    }

    public void saveUserInfo(String id, UserInfoDTO userInfoDTO) {
        Notification notification = notificationMap.get(Long.parseLong(id));
        notification.setUserName(userInfoDTO.getUserName());
        notification.setEmail(userInfoDTO.getEmail());
        notificationMap.put(Long.parseLong(id), notification);
    }

    @Scheduled(cron = "0 28 22 * * ?")
    private void saveAndSendNotifications() {
        log.info("saveNotifications");
        notificationId = -1;
        notifyRepository.deleteAll();
        notificationMap.forEach((k, v) -> {
            notifyRepository.save(v);
            emailService.sendEmail(v);
        });
        notificationMap.clear();
    }

}
