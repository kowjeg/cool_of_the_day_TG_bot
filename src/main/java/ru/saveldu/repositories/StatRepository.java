package ru.saveldu.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.saveldu.entities.Stat;


import java.util.List;
import java.util.Optional;

@Repository
public interface StatRepository extends JpaRepository<Stat,Long> {
    Optional<Stat> findByChatIdAndUserIdAndYear(long chatId, @NotNull Long userId, int year);

    List<Stat> findByChatIdAndYear(long chatId, int currentYear);
}
