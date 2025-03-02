package ru.saveldu.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDate;

@Data
@Entity
@Table(name = "cool_of_the_day")
public class CoolOfTheDay {
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
    @Column(name = "date", nullable = false)
    private LocalDate date;

    public CoolOfTheDay() {
    }


}