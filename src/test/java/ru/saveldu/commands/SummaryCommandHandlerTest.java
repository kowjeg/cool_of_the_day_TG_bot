package ru.saveldu.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.saveldu.api.DeepSeekApi;
import ru.saveldu.api.models.TextRequest;
import ru.saveldu.services.MessageService;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SummaryCommandHandlerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private DeepSeekApi deepSeekApi;

    @InjectMocks
    private SummaryCommandHandler command;

    @BeforeEach
    void setUp() {
        command.clearHistory();
    }


    private Update makeUpdate(String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);
        Chat chat = mock(Chat.class);
        when(message.getChat()).thenReturn(chat);
        when(chat.getUserName()).thenReturn("groupchat");
        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);
        when(message.getChatId()).thenReturn(1L);
        when(message.getFrom().getFirstName()).thenReturn("John");
        when(message.getText()).thenReturn(text);
        return update;
    }


    @Test
    void testExecute_SummaryOneMessage() throws IOException {

        command.addMessage(makeUpdate("Привет!"));

        when(deepSeekApi.apiRequestMethod(anyList()))
                .thenReturn("res sum");

        Update cmd = makeUpdate("/summary");
        command.execute(cmd);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TextRequest.Message>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(deepSeekApi).apiRequestMethod(captor.capture());

        List<TextRequest.Message> ctx = captor.getValue();

        assertThat(ctx).hasSize(2);
        assertThat(ctx.get(0).getRole()).isEqualTo("system");
        assertThat(ctx.get(1).getRole()).isEqualTo("user");
        assertThat(ctx.get(1).getContent())
                .isEqualTo("John: Привет!");

        verify(messageService).sendMessage(1L,
                "Суммаризация по 1 сообщениям:\nres sum");

    }

    @Test
    void testExecute_withInvalidNumber() throws IOException {
        command.execute(makeUpdate("/summary asd"));

        verifyNoInteractions(deepSeekApi);
        verify(messageService)
                .sendMessage(1L, "Ошибка: введите корректное число!");

    }


}



