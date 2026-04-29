package ru.sibsutis.pet_wearable.core.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sibsutis.pet_wearable.api.dto.PetDto;
import ru.sibsutis.pet_wearable.core.model.VitalData;
import ru.sibsutis.pet_wearable.core.service.PetCacheService;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class VitalDataGenerator {

    private final PetCacheService petCacheService;

    public List<VitalData> generateForAllPets() {
        List<VitalData> batch = new ArrayList<>();
        List<PetDto> pets = petCacheService.getAllPets();
        for (PetDto pet : pets) {
            batch.add(generateForPet(pet));
        }
        return batch;
    }

    private VitalData generateForPet(PetDto pet) {
        int heartRate = randomInRange(60, 120);
        int respiration = randomInRange(15, 30);

        if (ThreadLocalRandom.current().nextDouble() < 0.1) {
            heartRate = randomInRange(180, 220); // тахикардия
        }

        double homeLat = pet.getCollar().getHomeInfo().getLat();
        double homeLon = pet.getCollar().getHomeInfo().getLon();
        double lat = homeLat + randomOffset(0.02);  // ±2 км
        double lon = homeLon + randomOffset(0.02);

        double distance = calculateDistance(homeLat, homeLon, lat, lon);

        return VitalData.builder()
                .petId(pet.getId())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .heartRate(heartRate)
                .respiration(respiration)
                .distanceFromHome(distance)
                .timestamp(Instant.now().getEpochSecond())
                .build();
    }

    private int randomInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private double randomOffset(double range) {
        return ThreadLocalRandom.current().nextDouble(-range, range);
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
