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

    @Column(name = "carfee")
    String carFee;

    @Column(name = "waterfee")
    String waterFee;

    @Column(name = "motofee")
    String motoFee;

    @Column(name = "overtimefee")
    String overtimeFee;

    @Column(name = "electricityfee")
    String electricityFee;

    @Column(name = "deposit")
    String deposit;

    @Column(name = "payment")
    String payment;

    @Column(name = "renttime")
    String rentTime;

    @Column(name = "decorationtime")
    String decorationTime;

    @Column(name = "note")
    String note;

    // Quan hệ với bảng rentarea - One
    @OneToMany(mappedBy = "building",
            fetch = FetchType.LAZY,
            // PERSIST: save, MERGE: update
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    List<RentAreaEntity> rentAreaEntities = new ArrayList<>();

    // Quan hệ với bảng assignmentbuilding - ManyToMany
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "assignmentbuilding",
            joinColumns = @JoinColumn(name = "buildingid", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "staffid", nullable = false)
    )
    List<UserEntity> userEntities = new ArrayList<>();
}
