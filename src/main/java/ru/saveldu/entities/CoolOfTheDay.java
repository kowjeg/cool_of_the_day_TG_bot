package ru.saveldu.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @Column(name = "date", nullable = false)
    private LocalDate date;

    public CoolOfTheDay() {
    }


}