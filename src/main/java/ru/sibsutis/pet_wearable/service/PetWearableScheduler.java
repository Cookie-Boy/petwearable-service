package ru.sibsutis.pet_wearable.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.sibsutis.pet_wearable.generator.VitalDataGenerator;
import ru.sibsutis.pet_wearable.model.VitalData;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
@Component
public class PetWearableScheduler {
    private final VitalDataGenerator generator;
    private final MqttGateway mqttGateway;
    private final ObjectMapper objectMapper;

    @Value("${mqtt.topic}")
    private String mqttTopic;

    @Autowired
    public PetWearableScheduler(VitalDataGenerator generator,
                                MqttGateway mqttGateway,
                                ObjectMapper objectMapper) {
        this.generator = generator;
        this.mqttGateway = mqttGateway;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 60000) // каждую минуту
    public void generateAndPublish() {
        List<VitalData> allData = generator.generateForAllPets();
        for (VitalData data : allData) {
            try {
                String json = objectMapper.writeValueAsString(data);
                mqttGateway.sendToMqtt(json, mqttTopic);
                log.info("Vital data for pet with ID: {} sent.", data.getPetId());
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
            }
        }
    }
}
