package com.github.popovdmitry.licenseservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.popovdmitry.licenseservice.dto.LicenseFilterDTO;
import com.github.popovdmitry.licenseservice.service.LicenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    private final LicenseService licenseService;

    @KafkaListener(topics = "${kafka.topic.request-license-info-topic}")
    @SendTo
    public Object listen(ConsumerRecord<String, Object> request) {
        ObjectMapper objectMapper = new ObjectMapper();
        LicenseFilterDTO licenseFilterDTO = objectMapper.convertValue(request.value(), LicenseFilterDTO.class);

        if (new String(request.headers().headers("count").iterator().next().value()).equals("true")) {
            return licenseService.findAllByFilter(licenseFilterDTO).size();
        }
        return licenseService.findAllByFilter(licenseFilterDTO).toArray();
    }

}
