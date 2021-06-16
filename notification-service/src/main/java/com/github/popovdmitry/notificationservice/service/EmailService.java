package com.github.popovdmitry.notificationservice.service;

import com.github.popovdmitry.notificationservice.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getEmail());
        message.setSubject("License expires");
        message.setText(String.format("Hello dear, %s! " +
                "We inform you that your license for the \"%s\" product will expire in %d days.",
                notification.getUserName(),
                notification.getProductName(),
                (notification.getEndDate().getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24) ));
        javaMailSender.send(message);
    }
}
