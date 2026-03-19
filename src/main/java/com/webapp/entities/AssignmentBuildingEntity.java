package com.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "assignmentbuilding")
public class AssignmentBuildingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buildingid")
    BuildingEntity building;

    @ManyToOne
    @JoinColumn(name = "staffid")
    UserEntity staff;

}
