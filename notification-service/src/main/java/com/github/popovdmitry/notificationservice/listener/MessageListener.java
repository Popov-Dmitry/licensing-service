package com.github.popovdmitry.notificationservice.listener;

import com.github.popovdmitry.notificationservice.dto.LicenseInfoDTO;
import com.github.popovdmitry.notificationservice.dto.UserInfoDTO;
import com.github.popovdmitry.notificationservice.model.Notification;
import com.github.popovdmitry.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NotificationService notificationService;

    @KafkaListener(topics = "licensesInfoTopic", groupId = "licensesInfo", containerFactory = "licenseInfoListener")
    void kafkaLicenseListener(ConsumerRecord<String, LicenseInfoDTO> consumerRecord) {
        kafkaTemplate.send("usersTopic", notificationService.saveLicenseInfo(consumerRecord.value()).toString(), consumerRecord.key());
    }

    @KafkaListener(topics = "usersInfoTopic", groupId = "usersInfo", containerFactory = "usersInfoListener")
    void kafkaUsersListener(ConsumerRecord<String, UserInfoDTO> consumerRecord) {
        notificationService.saveUserInfo(consumerRecord.key(), consumerRecord.value());
    }
}