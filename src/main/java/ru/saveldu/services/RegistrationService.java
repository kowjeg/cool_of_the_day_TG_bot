package ru.saveldu.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.saveldu.entities.Users;
import ru.saveldu.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;


    public boolean isUserAlreadyRegistered(long userId, long chatId) {
        return userRepository.findByChatIdAndUserId(chatId, userId).isPresent();
    }

    public Users registerUser(long userId, long chatId, String userName) {
        Users newUser = new Users();
        newUser.setUserId(userId);
        newUser.setChatId(chatId);
        newUser.setUserName(userName);
        return userRepository.save(newUser);
    }

}
