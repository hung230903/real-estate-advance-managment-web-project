package com.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rentarea")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentAreaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "value")
  Long value;

  @ManyToOne
  @JoinColumn(name = "buildingid")
  BuildingEntity building;
}
