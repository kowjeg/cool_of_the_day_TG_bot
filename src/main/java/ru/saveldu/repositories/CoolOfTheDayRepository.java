package ru.saveldu.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.saveldu.entities.CoolOfTheDay;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CoolOfTheDayRepository extends JpaRepository<CoolOfTheDay, Integer> {

    Optional<CoolOfTheDay> findByChatIdAndDate(long chatId, LocalDate today);
}
