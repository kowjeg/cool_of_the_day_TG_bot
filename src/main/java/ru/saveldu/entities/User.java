package ru.saveldu.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Data
@Entity
@Table(name = "users")
public class User {
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

    @Column(name = "comb_size")
    private Integer combSize;

    @Column(name = "last_played_date")
    private LocalDate lastPlayedDate;

    public User() {
    }


}