package ru.sibsutis.pet_wearable.api.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.sibsutis.pet_wearable.api.dto.PetDto;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileServiceClient {
    private final RestClient restClient;
    private final TokenProvider tokenProvider;

    public List<PetDto> getAllPets() {
        String token = tokenProvider.getFreshToken();
        log.info("Trying to get new pets from profile-service");
        log.info("Fresh token: {}", token);
        return restClient.get()
                .uri("/api/profile/pets")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}

