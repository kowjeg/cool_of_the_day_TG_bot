package ru.saveldu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoSendService {

    private final MessageService messageService;

    private static final Map<String, String> photoFileIdCache = new ConcurrentHashMap<>();


    public void sendPhoto(long chatId, String imageKey) {

        File photoFile = new File(imageKey);
        if (!photoFile.exists()) {
//            messageService.sendMessage(chatId, "Файл не найден на сервере.");
            log.warn("Файл \"{}\" не найден на сервере.", imageKey);
            return;
        }

        try {
            // Если в кеше уже есть file_id для этой картинки, отправляем его
            if (photoFileIdCache.containsKey(imageKey)) {
                String cachedFileId = photoFileIdCache.get(imageKey);
                SendPhoto sendPhotoById = SendPhoto.builder()
                        .chatId(chatId)
                        .photo(new InputFile(cachedFileId))

                        .build();
                log.info("Повторная отправка \"{}\", используя file_id \"{}\", закешированного на сервере телеграма", imageKey, cachedFileId);
                messageService.sendPhoto(chatId, sendPhotoById);
                return;
            }


            SendPhoto sendPhotoByFile = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(new InputFile(photoFile))

                    .build();

            Message response = messageService.sendPhoto(chatId, sendPhotoByFile);

            log.info("Первая отправка картинки (загружаем файл)");


            if (response != null && response.hasPhoto()) {
                PhotoSize largestPhoto = response.getPhoto()
                        .stream()
                        .max(Comparator.comparing(PhotoSize::getFileSize))
                        .orElse(null);

                if (largestPhoto != null) {
                    String newFileId = largestPhoto.getFileId();
                    photoFileIdCache.put(imageKey, newFileId);
                    log.info("Сохранили в кеш file_id для {} = {}", imageKey, newFileId);
                }
            }

        } catch (Exception e) {
            log.error("Ошибка при отправке фото в PhotoSendService: ", e);
            messageService.sendMessage(chatId, "Не удалось отправить фото.");
        }




    }
    public Message sendPhotoMessage(long chatId, String imagePatch) {


        File photoFile = new File(imagePatch);

        if (!photoFile.exists()) {
            log.warn("File {} does not exist.", imagePatch);
            return null;
        }
        SendPhoto sendPhoto = new SendPhoto(String.valueOf(chatId), new InputFile());

//        Message response = messageService.sendPhoto(chatId, )


        return null;

    }
}
