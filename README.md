#### Working solution for 2025 version 

Answer copied from stackoverflow[https://stackoverflow.com/a/75680634/10531665], here from the answer `BROKER` is replaced with `NODE`, 
everything remains same

````yaml
services:
  kafka:
    image: 'bitnami/kafka:latest'
    ports:
      - '9094:9094'

    environment:
      - KAFKA_ENABLE_KRAFT=yes
      - KAFKA_CFG_NODE_ID=1
      - KAFKA_CFG_PROCESS_ROLES=broker,controller
      - KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094
      - KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,EXTERNAL:PLAINTEXT
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094
      - KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=1@:9093
      - ALLOW_PLAINTEXT_LISTENER=yes
````


2025 April update, 

For the Producer/Consumer properties file, everything remains same except, the port will be `9094`


#### Bitnami Kafka Kraft version without Zookeeper

This example shows how to use Bitnami Kafka, with Kraft, without additional Zookeeper

Boilerplate yml for firing Kafka

````yml
services:
  kafka:
    image: docker.io/bitnami/kafka:latest
#    container_name: kafka
    ports:
      - "9092:9092"
    volumes:
      - "kafka_data:/bitnami"
    environment:
      # KRaft settings
      - KAFKA_CFG_NODE_ID=0
      - KAFKA_CFG_PROCESS_ROLES=controller,broker
      - KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093
      # Listeners
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://:9092
      - KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
      - KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER
      - KAFKA_CFG_INTER_BROKER_LISTENER_NAME=PLAINTEXT
#  volumes:
#    kafka_data:
#      driver: local
volumes:
  kafka_data:
    driver: local

````

Kafka Producer code

Properties file

````properties
spring.application.name=producer1
server.port=8082
spring.kafka.bootstrap-servers=localhost:9092
app.topic-name=topic13
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
````

````java
//controller for producer

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

````

````java
//producer code
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

````

Kafka Consumer code

````properties
spring.application.name=consumer1
server.port=8083
spring.kafka.bootstrap-servers=localhost:9092
app.topic-name=topic13
spring.kafka.consumer.group-id=group-alpha
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
````


````java
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

````