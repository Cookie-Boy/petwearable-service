package ru.sibsutis.pet_wearable.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {
    private Double lat;
    private Double lon;
    private Double distanceFromHome;
}
