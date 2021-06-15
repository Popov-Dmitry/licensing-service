package com.github.popovdmitry.notificationservice.config;

import com.github.popovdmitry.notificationservice.dto.LicenseInfoDTO;
import com.github.popovdmitry.notificationservice.dto.UserInfoDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, LicenseInfoDTO> consumerFactoryLicense(){
        Map<String,Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"licensesInfo");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<LicenseInfoDTO>(LicenseInfoDTO.class, false));
    }

    @Bean
    public ConsumerFactory<String, UserInfoDTO> consumerFactoryUser(){
        Map<String,Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"usersInfo");
        return new DefaultKafkaConsumerFactory<>(config,new StringDeserializer(), new JsonDeserializer<UserInfoDTO>(UserInfoDTO.class, false));
    }

    @Bean(name = "licenseInfoListener")
    public ConcurrentKafkaListenerContainerFactory<String, LicenseInfoDTO> licenseInfoListener(){
        ConcurrentKafkaListenerContainerFactory<String, LicenseInfoDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryLicense());
        return factory;
    }

    @Bean(name = "userInfoListener")
    public ConcurrentKafkaListenerContainerFactory<String, UserInfoDTO> userInfoListener(){
        ConcurrentKafkaListenerContainerFactory<String, UserInfoDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryUser());
        return factory;
    }
}