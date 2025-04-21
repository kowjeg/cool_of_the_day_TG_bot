package ru.saveldu.repositories;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.saveldu.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> getUsersByChatId(long chatId);

    List<User> findTop10ByChatIdAndCombSizeIsNotNullOrderByCombSizeDesc(long chatId);

    List<User> findByChatIdOrderByCombSizeDesc(long chatId);

    Optional<User> findByChatIdAndUserId(long chatId, long userId);
}
