package com.homeofthewizard.horseracemanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

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
    @NotNull
    private String name;

    @Column
    @NotNull
    private LocalDate date;

    @Column
    @NotNull
    private Integer number;

    @OneToMany(mappedBy = "race", fetch = FetchType.LAZY, cascade = ALL)
    @OrderColumn(name="noHorse")
    private List<RaceHorse> horses;
}
