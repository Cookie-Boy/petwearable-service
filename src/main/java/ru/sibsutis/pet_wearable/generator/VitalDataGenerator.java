package ru.sibsutis.pet_wearable.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.sibsutis.pet_wearable.model.VitalData;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
// Животные дожны браться из БД (Mongo), координаты должны рассчитываться на основе параметра
// 'home_coords' у животных (надо будет указывать при регистрации питомца)
public class VitalDataGenerator {
    // Список питомцев: можно загружать из БД, но для простоты — статический
    private static final List<PetInfo> PETS = List.of(
            new PetInfo("pet1", "beagle"),
            new PetInfo("pet2", "labrador"),
            new PetInfo("pet3", "dachshund")
    );

    @Value("${home.latitude}")
    private double homeLat;

    @Value("${home.longitude}")
    private double homeLon;

    // Генерация данных для всех питомцев
    public List<VitalData> generateForAllPets() {
        List<VitalData> batch = new ArrayList<>();
        for (PetInfo pet : PETS) {
            batch.add(generateForPet(pet));
        }
        return batch;
    }

    private VitalData generateForPet(PetInfo pet) {
        // Случайные показатели в пределах нормы (с возможностью аномалий)
        int heartRate = randomInRange(60, 120);
        int respiration = randomInRange(15, 30);

        // Иногда создаём аномалии (шанс 10%)
        if (ThreadLocalRandom.current().nextDouble() < 0.1) {
            heartRate = randomInRange(180, 220); // тахикардия
        }

        // Координаты около дома с небольшим разбросом (но могут быть и далеко)
        double lat = homeLat + randomOffset(0.02);  // примерно ±2 км
        double lon = homeLon + randomOffset(0.02);

        return VitalData.builder()
                .petId(pet.id())
                .breed(pet.breed())
                .heartRate(heartRate)
                .respiration(respiration)
                .latitude(lat)
                .longitude(lon)
                .timestamp(Instant.now().getEpochSecond())
                .build();
    }

    private int randomInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private double randomOffset(double range) {
        return ThreadLocalRandom.current().nextDouble(-range, range);
    }

    private record PetInfo(String id, String breed) {}
}
