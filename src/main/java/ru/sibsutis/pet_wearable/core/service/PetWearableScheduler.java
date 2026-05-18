package ru.sibsutis.pet_wearable.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.sibsutis.pet_wearable.core.generator.VitalDataGenerator;
import ru.sibsutis.pet_wearable.core.model.Location;
import ru.sibsutis.pet_wearable.core.model.VitalData;
import ru.sibsutis.pet_wearable.proto.VitalDataProto;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetWearableScheduler {
    private final VitalDataGenerator generator;
    private final MqttGateway mqttGateway;

    @Value("${mqtt.topic}")
    private String mqttTopic;

    @Scheduled(fixedDelay = 60_000)
    public void generateAndPublish() {
        List<VitalData> allData = generator.generateForAllPets();
        for (VitalData data : allData) {
            try {
                VitalDataProto.VitalDataMessage message = buildVitalDataMessage(data);
                byte[] payload = message.toByteArray();
                mqttGateway.sendToMqtt(payload, mqttTopic);
                log.info("Vital data for pet with ID: {} sent.", data.getPetId());
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
            }
        }
    }

    private VitalDataProto.VitalDataMessage buildVitalDataMessage(VitalData data) {
        VitalDataProto.VitalDataMessage.Builder builder = VitalDataProto.VitalDataMessage.newBuilder()
                .setPetId(data.getPetId() != null ? data.getPetId() : "")
                .setSpecies(data.getSpecies() != null ? data.getSpecies() : "")
                .setBreed(data.getBreed() != null ? data.getBreed() : "")
                .setHeartRate(data.getHeartRate() != null ? data.getHeartRate() : 0)
                .setRespiration(data.getRespiration() != null ? data.getRespiration() : 0)
                .setTemperature(data.getTemperature() != null ? data.getTemperature() : 0.0)
                .setTimestamp(data.getTimestamp());

        Location loc = data.getLocation();
        if (loc != null) {
            VitalDataProto.Location pbLocation = VitalDataProto.Location.newBuilder()
                    .setLat(loc.getLat() != null ? loc.getLat() : 0.0)
                    .setLon(loc.getLon() != null ? loc.getLon() : 0.0)
                    .setDistanceFromHome(loc.getDistanceFromHome() != null ? loc.getDistanceFromHome() : 0.0)
                    .setDistanceLimit(loc.getDistanceLimit())
                    .build();
            builder.setLocation(pbLocation);
        }

        return builder.build();
    }
}
