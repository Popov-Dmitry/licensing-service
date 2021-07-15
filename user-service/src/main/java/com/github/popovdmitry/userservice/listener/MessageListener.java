package com.github.popovdmitry.userservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.popovdmitry.userservice.dto.KafkaUserInfoDTO;
import com.github.popovdmitry.userservice.dto.UserFilterDTO;
import com.github.popovdmitry.userservice.model.User;
import com.github.popovdmitry.userservice.service.UserService;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class MessageListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserService userService;

    @Value("${kafka.topic.usersInfoTopic}")
    private String usersInfoTopic;

    public MessageListener(@Qualifier("defaultKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate,
                           UserService userService) {
        this.kafkaTemplate = kafkaTemplate;
        this.userService = userService;
    }

    @KafkaListener(topics = "${kafka.topic.usersTopic}",
            groupId = "${kafka.consumer-group.userId}", containerFactory = "userIdListener")
    void kafkaUserIdListener(ConsumerRecord<String, String> consumerRecord) {
        try {
            User user = userService.findUser(Long.parseLong(consumerRecord.value()));
            kafkaTemplate.send(usersInfoTopic, consumerRecord.key(),
                    new KafkaUserInfoDTO(user.getEmails().get(0).getEmail(), user.getName()));
        } catch (NotFoundException exception) {
            exception.printStackTrace();
        }
    }

    @KafkaListener(topics = "${kafka.topic.request-user-info-topic}")
    @SendTo
    public Object listen(ConsumerRecord<String, Object> request) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserFilterDTO userFilterDTO = objectMapper.convertValue(request.value(), UserFilterDTO.class);

        if (new String(request.headers().headers("count").iterator().next().value()).equals("true")) {
            return userService.findAllByFilter(userFilterDTO).size();
        }
        return userService.findAllByFilter(userFilterDTO).toArray();
    }
}