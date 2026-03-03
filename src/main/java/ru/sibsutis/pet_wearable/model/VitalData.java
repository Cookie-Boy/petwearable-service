package ru.sibsutis.pet_wearable.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Сделать больше различных полей
public class VitalData {
    private String petId;
    private String breed;
    private Integer heartRate;
    private Integer respiration;
    private Double latitude;
    private Double longitude;
    private Long timestamp;

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp != null ? timestamp : Instant.now().getEpochSecond();
    }
}
