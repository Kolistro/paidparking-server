package ru.omgu.paidparking_server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.omgu.paidparking_server.validation.annotation.ValidParkingSpots;
import ru.omgu.paidparking_server.validation.annotation.ValidWorkingHours;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "building")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ValidWorkingHours
@ValidParkingSpots
public class BuildingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Size(max = 100, message = "Название объекта не должно превышать 100 символов.")
    @Column(unique = true)
    private String locationName;
    @NotBlank(message = "Адрес не может быть пустым")
    @Size(min = 5, max = 255, message = "Адрес должен содержать от 5 до 255 символов.")
    private String address;
    private Long totalParkingSpots;
    private Long availableParkingSpots;
    private Long costPerHour;
    private LocalTime workingHoursStart;
    private LocalTime workingHoursEnd;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservations = new ArrayList<>();
}
