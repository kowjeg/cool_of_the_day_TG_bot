package ru.saveldu.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.saveldu.entities.User;
import ru.saveldu.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;


    public boolean isUserAlreadyRegistered(long userId, long chatId) {
        return userRepository.findByChatIdAndUserId(chatId, userId).isPresent();
    }

    public User registerUser(long userId, long chatId, String userName) {
        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setChatId(chatId);
        newUser.setUserName(userName);
        return userRepository.save(newUser);
    }

}
