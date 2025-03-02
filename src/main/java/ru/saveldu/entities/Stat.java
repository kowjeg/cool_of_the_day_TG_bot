package ru.saveldu.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;



@Data
@Entity
@Table(name = "stats")
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;


    @NotNull
    @Column(name = "chat_id", nullable = false)
    private Long chatId;


    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Size(max = 255)
    @NotNull
    @Column(name = "user_name", nullable = false)
    private String userName;

    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;


    @NotNull
    @Column(name = "count_wins", nullable = false)
    private Integer countWins;

    public Stat() {
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Stat;
    }

}