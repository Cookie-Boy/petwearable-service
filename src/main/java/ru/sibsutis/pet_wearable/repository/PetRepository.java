package ru.sibsutis.pet_wearable.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.sibsutis.pet_wearable.model.Pet;

import java.util.Optional;

public interface PetRepository extends MongoRepository<Pet, String> {
    Optional<Pet> findByQrCode(String qrCode);
    Optional<Pet> findByChipNumber(String chipNumber);
}