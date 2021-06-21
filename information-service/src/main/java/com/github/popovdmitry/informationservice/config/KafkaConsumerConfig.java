package com.github.popovdmitry.informationservice.config;

import com.github.popovdmitry.informationservice.dto.LicenseFilterDTO;
import com.github.popovdmitry.informationservice.dto.UserFilterDTO;
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
    public ConsumerFactory<String, Object> consumerFactoryLicenseFilter(){
        Map<String,Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"licenseFilter");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<Object>(Object.class, false));
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactoryUserFilter(){
        Map<String,Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"userFilter");
        return new DefaultKafkaConsumerFactory<>(config,new StringDeserializer(), new JsonDeserializer<Object>(Object.class, false));
    }

    @Bean(name = "licenseFilterListener")
    public ConcurrentKafkaListenerContainerFactory<String, Object> licenseFilterListener(){
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryLicenseFilter());
        return factory;
    }

    @Bean(name = "userFilterListener")
    public ConcurrentKafkaListenerContainerFactory<String, Object> userFilterListener(){
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryUserFilter());
        return factory;
    }
}