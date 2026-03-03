package ru.sibsutis.pet_wearable.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.sibsutis.pet_wearable.generator.VitalDataGenerator;
import ru.sibsutis.pet_wearable.model.VitalData;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
// Переделать систему с координатами
public class PetWearableScheduler {
    private final VitalDataGenerator generator;
    private final MqttGateway mqttGateway;
    private final ObjectMapper objectMapper;

    @Value("${home.latitude}")
    private double homeLat;

    @Value("${home.longitude}")
    private double homeLon;

    @Value("${home.max-distance-km}")
    private double maxDistanceKm;

    @Value("${thresholds.heart-rate-max}")
    private int maxHeartRate;

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
            processOne(data);
        }
    }

    private void processOne(VitalData data) {
        // 1. Проверка критических аномалий
        boolean critical = false;
        StringBuilder alertMessage = new StringBuilder("🚨 " + data.getPetId() + ": ");

        if (data.getHeartRate() > maxHeartRate) {
            alertMessage.append("пульс ").append(data.getHeartRate()).append("! ");
            critical = true;
        }

        double distance = calculateDistance(
                homeLat, homeLon,
                data.getLatitude(), data.getLongitude()
        );
        if (distance > maxDistanceKm) {
            alertMessage.append(String.format("удалился на %.1f км! ", distance));
            critical = true;
        }

        if (critical) {
//            telegramService.sendAlert(alertMessage.toString());
            return;
        }

        // 2. Публикуем в MQTT
        try {
            String json = objectMapper.writeValueAsString(data);
            mqttGateway.sendToMqtt(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Упрощённая формула гаверсинуса для расстояния в км
        final int R = 6371; // радиус Земли в км
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
