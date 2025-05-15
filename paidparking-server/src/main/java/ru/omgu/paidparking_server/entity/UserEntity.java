package ru.omgu.paidparking_server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.omgu.paidparking_server.validation.annotation.ValidName;
import ru.omgu.paidparking_server.validation.annotation.ValidPassword;
import ru.omgu.paidparking_server.validation.annotation.ValidPhoneNumber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @ValidName
    private String firstName;
    @ValidName
    private String lastName;
    @Column(unique = true)
    @ValidPhoneNumber
    @NotNull
    private String phoneNumber;
    @ValidPassword
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_car",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "car_id"))
    private Set<CarEntity> cars = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationEntity> reservations = new ArrayList<>();

}
