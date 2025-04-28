package com.example.consumer1.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    @KafkaListener(topics="${app.topic-name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumerMessage(String message){
        System.out.println("Message received "+message);
        logger.info("Message received {}",message);
    }
}
