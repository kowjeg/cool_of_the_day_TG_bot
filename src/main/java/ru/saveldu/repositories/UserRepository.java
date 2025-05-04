package ru.saveldu.repositories;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.saveldu.entities.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    List<Users> getUsersByChatId(long chatId);

    List<Users> findTop10ByChatIdAndCombSizeIsNotNullOrderByCombSizeDesc(long chatId);

    List<Users> findByChatIdOrderByCombSizeDesc(long chatId);

    Optional<Users> findByChatIdAndUserId(long chatId, long userId);
}
