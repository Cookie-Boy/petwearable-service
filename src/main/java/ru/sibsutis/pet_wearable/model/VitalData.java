package ru.sibsutis.pet_wearable.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VitalData {
    private String petId;
    private String species;
    private String breed;

    private Integer heartRate;
    private Integer respiration;
    private Double temperature;

    private Double distanceFromHome;

    private Long timestamp;

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp != null ? timestamp : Instant.now().getEpochSecond();
    }
}