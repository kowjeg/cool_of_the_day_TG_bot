    package ru.saveldu.services;

    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.ArgumentCaptor;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import ru.saveldu.entities.Users;
    import ru.saveldu.repositories.UserRepository;

    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;


    @ExtendWith(MockitoExtension.class)
    class RegistrationServiceTest {

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private RegistrationService registrationService;

        @Test
        void registerUser_savesUserAndReturnIt() {
            Users savedUser = new Users();
            savedUser.setUserId(2L);
            savedUser.setChatId(1L);
            savedUser.setUserName("Tester");

            when(userRepository.save(any(Users.class))).thenReturn(savedUser);

            Users resultUser = registrationService.registerUser(2L, 1L, "Tester");

            ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
            verify(userRepository).save(captor.capture());
            Users passedUser = captor.getValue();
            assertEquals(2L, passedUser.getUserId());
            assertEquals(1L, passedUser.getChatId());
            assertEquals("Tester", passedUser.getUserName());

            assertEquals(savedUser, resultUser);

        }

        @Test
        void isUserAlreadyRegistered_whenPresent_returnsTrue() {
            when(userRepository.findByChatIdAndUserId(1L, 2L)).thenReturn(Optional.of(new Users()));
            assertTrue(registrationService.isUserAlreadyRegistered(2L, 1L));
        }

        @Test
        void isUserAlreadyRegistered_whenNotPresent_returnsFalse() {
            when(userRepository.findByChatIdAndUserId(1L, 2L)).thenReturn(Optional.empty());
            assertFalse(registrationService.isUserAlreadyRegistered(2L, 1L));
        }




    }