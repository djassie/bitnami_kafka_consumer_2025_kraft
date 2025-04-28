package com.example.producer1.controller;

import com.example.producer1.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/v1/kafka")
public class MessageController {
    private final KafkaProducer kafkaProducer;
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);
    public MessageController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

//    @PostMapping("/send")
    @RequestMapping(path = "send", method = RequestMethod.POST)
    public void sendMessageToKafka(@RequestBody String message) {
        kafkaProducer.sendMessage(message);
        logger.info("Message {} called from controller", message);
    }
}
