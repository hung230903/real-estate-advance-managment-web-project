package com.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "building")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", length = 255, nullable = false)
    String name;

    @Column(name = "street", length = 255, nullable = false)
    String street;

    @Column(name = "ward", length = 255, nullable = false)
    String ward;

    @Column(name = "district", length = 255, nullable = false)
    String district;

    @Column(name = "rentprice", nullable = false)
    Double rentPrice;

    @Column(name = "managername")
    String managerName;

    @Column(name = "managerphone", length = 12)
    String managerPhone;

    @Column(name = "numberofbasement")
    Long numberOfBasement;

    @Column(name = "floorarea")
    Long floorArea;

    @Column(name = "brokeragefee")
    Double brokerageFee;

    @Column(name = "servicefee")
    String serviceFee;

    @Column(name = "structure")
    String structure;

    @Column(name = "type")
    String typeCode;

    @Column(name = "level")
    String level;

    @Column(name = "rentpricedescription")
    String rentPriceDescription;

    @Column(name = "direction")
    String direction;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    List<RentAreaEntity> rentAreaEntities = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "assignmentbuilding",
            joinColumns = @JoinColumn(name = "buildingid", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "staffid", nullable = false)
    )
    List<UserEntity> userEntities = new ArrayList<>();
}
