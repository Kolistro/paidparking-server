package ru.omgu.paidparking_server.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.omgu.paidparking_server.enums.CarColor;
import ru.omgu.paidparking_server.validation.annotation.ValidCarNumber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "car")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(unique = true)
    @ValidCarNumber
    private String carNumber;
    @Column(nullable = true)
    private String brand;
    @Column(nullable = true)
    private String model;
    @Enumerated(EnumType.STRING)
    private CarColor color = CarColor.UNDEFINED;

    @ManyToMany(mappedBy = "cars")
    private Set<UserEntity> users = new HashSet<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservations = new ArrayList<>();
}
