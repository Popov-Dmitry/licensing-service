package com.github.popovdmitry.informationservice.service;

import com.github.popovdmitry.informationservice.dto.LicenseFilterDTO;
import com.github.popovdmitry.informationservice.dto.UserFilterDTO;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class InformationService {

    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    @Value("${kafka.topic.request-user-info-topic}")
    private String requestUserInfoTopic;

    @Value("${kafka.topic.request-license-info-topic}")
    private String requestLicenseInfoTopic;

    @Value("${kafka.topic.requestreply-topic}")
    private String requestReplyTopic;

    public Object getLicenseInfo(LicenseFilterDTO licenseFilterDTO, boolean count) throws ExecutionException, InterruptedException {
        ProducerRecord<String, Object> recordLicense = new ProducerRecord<String, Object>(requestLicenseInfoTopic, licenseFilterDTO);
        if (count) {
            return requestReplyCount(recordLicense);
        }
        return requestReply(recordLicense);
    }

    public Object getUserInfo(UserFilterDTO userFilterDTO, boolean count) throws ExecutionException, InterruptedException {
        ProducerRecord<String, Object> recordUser = new ProducerRecord<String, Object>(requestUserInfoTopic, userFilterDTO);
        if (count) {
            return requestReplyCount(recordUser);
        }
        return requestReply(recordUser);
    }

    private ArrayList<Object> requestReply(ProducerRecord<String, Object> record) throws ExecutionException, InterruptedException {
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
        record.headers().add(new RecordHeader("count", "false".getBytes()));
        RequestReplyFuture<String, Object, Object> sendAndReceive = replyingKafkaTemplate.sendAndReceive(record);
        SendResult<String, Object> sendResult = sendAndReceive.getSendFuture().get();
        return (ArrayList<Object>) sendAndReceive.get().value();
    }

    private Object requestReplyCount(ProducerRecord<String, Object> record) throws ExecutionException, InterruptedException {
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
        record.headers().add(new RecordHeader("count", "true".getBytes()));
        RequestReplyFuture<String, Object, Object> sendAndReceive = replyingKafkaTemplate.sendAndReceive(record);
        SendResult<String, Object> sendResult = sendAndReceive.getSendFuture().get();
        return sendAndReceive.get().value();
    }

}
