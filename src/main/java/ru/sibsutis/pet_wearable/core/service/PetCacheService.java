package ru.sibsutis.pet_wearable.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.sibsutis.pet_wearable.api.client.ProfileServiceClient;
import ru.sibsutis.pet_wearable.api.dto.PetDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetCacheService {

    private final ProfileServiceClient profileServiceClient;

    @Cacheable(value = "allPets", unless = "#result == null || #result.isEmpty()")
    public List<PetDto> getAllPets() {
        return profileServiceClient.getAllPets();
    }

    @Scheduled(fixedDelay = 300_000)
    @CacheEvict(value = "allPets", allEntries = true)
    public void evictCache() {
        log.info("Evicting pets cache");
    }
}