package ru.sibsutis.pet_wearable.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "pets")
@Data
public class Pet {
    @Id
    private String id;

    private String ownerId;
    private String species;
    private Integer age;
    private String breed;
    private String chipNumber;

    private MedicalRecord medicalRecord;
    private Collar collar;

    @Indexed(unique = true)
    private String qrCode;

    @Data
    public static class MedicalRecord {
        private List<Vaccination> vaccinations;
        private List<String> allergies;
        private List<String> chronicDiseases;

        @Data
        public static class Vaccination {
            private String name;
            private LocalDate date;
            private LocalDate nextDueDate;
        }
    }

    @Data
    public static class Collar {
        private Boolean active;
        private HomeInfo homeInfo;

        @Data
        public static class HomeInfo {
            private Boolean alerting;
            private Integer radius;
            private Double lat;
            private Double lon;
        }
    }
}