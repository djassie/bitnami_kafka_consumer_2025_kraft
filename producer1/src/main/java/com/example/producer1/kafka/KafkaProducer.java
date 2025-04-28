package com.example.producer1.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${app.topic-name}")
    private String KafkaTopic;
    Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message){
        kafkaTemplate.send(KafkaTopic, message);
        System.out.println("Message "+message+" has been sent");
        log.info("Kafka Message {} has been sent", message);
    }
}
