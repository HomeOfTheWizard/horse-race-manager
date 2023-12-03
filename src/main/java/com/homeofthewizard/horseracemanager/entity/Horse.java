package com.homeofthewizard.horseracemanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(
        name="horses",
        uniqueConstraints = {@UniqueConstraint(name = "UniqueName", columnNames = { "name" })}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Horse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @OneToMany(mappedBy = "horse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RaceHorse> races;

}
