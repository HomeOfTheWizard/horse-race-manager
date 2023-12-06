package com.homeofthewizard.horseracemanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name="races",
        uniqueConstraints = {
            @UniqueConstraint(name = "UniqueDateAndName", columnNames = { "date", "name" }),
            @UniqueConstraint(name = "UniqueDateAndNumber", columnNames = { "date", "number" })
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Race implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private LocalDate date;

    @Column
    private Integer number;

    @OneToMany(mappedBy = "race", fetch = FetchType.LAZY)
    @OrderColumn(name="noHorse")
    private List<RaceHorse> horses;
}
