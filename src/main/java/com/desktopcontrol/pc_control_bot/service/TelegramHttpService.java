package com.desktopcontrol.pc_control_bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramHttpService {

    private final HttpClient httpClient;

    @Value("${spring.telegram.bot.token}")
    private String botToken;

    public TelegramHttpService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendTextMessage(Long chatId, String text) {
        sendTextMessageWithKeyboard(chatId, text, null);
    }

    public void sendTextMessageWithKeyboard(Long chatId, String text, String jsonKeyboard) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{")
                .append("\"chat_id\":").append(chatId).append(",")
                .append("\"text\":\"").append(text.replace("\n", "\\n")).append("\"");

        if (jsonKeyboard != null && !jsonKeyboard.isEmpty()) {
            jsonBuilder.append(",\"reply_markup\":").append(jsonKeyboard);
        }
        jsonBuilder.append("}");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBuilder.toString()))
                .build();
        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.err.println("Помилка відправки тексту з клавіатурою: " + e.getMessage());
        }
    }

    public void sendPhotoMessage(Long chatId, File file) {
        String boundary = "282626213711985392634351";
        String url = "https://api.telegram.org/bot" + botToken + "/sendPhoto";
        try {
            List<byte[]> byteArrays = new ArrayList<>();
            byteArrays.add(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"chat_id\"\r\n\r\n" + chatId + "\r\n").getBytes());
            byteArrays.add(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"photo\"; filename=\"" + file.getName() + "\"\r\nContent-Type: image/png\r\n\r\n").getBytes());
            byteArrays.add(Files.readAllBytes(file.toPath()));
            byteArrays.add(("\r\n--" + boundary + "--\r\n").getBytes());

            long totalLength = 0;
            for (byte[] arr : byteArrays) totalLength += arr.length;
            byte[] totalBytes = new byte[(int) totalLength];
            int currentPos = 0;
            for (byte[] arr : byteArrays) {
                System.arraycopy(arr, 0, totalBytes, currentPos, arr.length);
                currentPos += arr.length;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(totalBytes))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.err.println("Помилка відправки фото: " + e.getMessage());
        }
    }
    public void sendInlineMarkup(Long chatId, String text, String jsonInlineKeyboard) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        String jsonBody = "{"
                + "\"chat_id\":" + chatId + ","
                + "\"text\":\"" + text.replace("\n", "\\n") + "\","
                + "\"reply_markup\":" + jsonInlineKeyboard
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.err.println("Помилка відправки інлайн-кнопок: " + e.getMessage());
        }
    }

    public void editInlineMarkup(Long chatId, Integer messageId, String jsonInlineKeyboard) {
        String url = "https://api.telegram.org/bot" + botToken + "/editMessageReplyMarkup";

        String jsonBody = "{"
                + "\"chat_id\":" + chatId + ","
                + "\"message_id\":" + messageId + ","
                + "\"reply_markup\":" + jsonInlineKeyboard
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.err.println("Помилка редагування інлайн-кнопок: " + e.getMessage());
        }
    }

}