package com.github.popovdmitry.userservice.listener;

import com.github.popovdmitry.userservice.dto.KafkaUserInfoDTO;
import com.github.popovdmitry.userservice.model.User;
import com.github.popovdmitry.userservice.service.UserService;
import javassist.NotFoundException;
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

    private final KafkaTemplate<String, KafkaUserInfoDTO> kafkaTemplate;
    private final UserService userService;

    @KafkaListener(topics = "usersTopic", groupId = "userId", containerFactory = "userIdListener")
    void kafkaUserIdListener(ConsumerRecord<String, String> consumerRecord) {
        try {
            User user = userService.findUser(Long.parseLong(consumerRecord.value()));
            kafkaTemplate.send("usersInfoTopic", consumerRecord.key(),
                    new KafkaUserInfoDTO(user.getEmails().get(0).getEmail(), user.getName()));
        } catch (NotFoundException exception) {
            exception.printStackTrace();
        }
    }
}