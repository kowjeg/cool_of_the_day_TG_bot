package ru.saveldu.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "stats")
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;


    @NotNull
    @NotNull
    @Column(name = "chat_id", nullable = false)
    private Long chatId;


    @NotNull
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Size(max = 255)@NotNull
    @Size(max = 255)
    @NotNull
    @Column(name = "user_name", nullable = false)
    private String userName;

    @NotNull
    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;


    @NotNull
    @NotNull
    @Column(name = "count_wins", nullable = false)
    private Integer countWins;

    public Stat() {
    }


    protected boolean canEqual(final Object other) {
        return other instanceof Stat;
    }

}