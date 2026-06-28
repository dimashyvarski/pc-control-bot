package com.desktopcontrol.pc_control_bot.command;

import com.desktopcontrol.pc_control_bot.service.ComputerControlService;
import com.desktopcontrol.pc_control_bot.service.TelegramHttpService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import java.io.File;

@Component
public class ScreenshotCommand implements BotCommand {

    private final ComputerControlService computerControlService;
    private final TelegramHttpService telegramHttpService;

    public ScreenshotCommand(ComputerControlService computerControlService, TelegramHttpService telegramHttpService) {
        this.computerControlService = computerControlService;
        this.telegramHttpService = telegramHttpService;
    }

    @Override
    public boolean canExecute(String text) {
        return text.equalsIgnoreCase("SCREENSHOT") || text.equalsIgnoreCase("Скрін");
    }

    @Override
    public void execute(Message message) {
        Long chatId = message.getChatId();
        telegramHttpService.sendTextMessage(chatId, "📸 Роблю скріншот.");
        File file = computerControlService.makeScreenshot();
        if (file != null && file.exists()) {
            telegramHttpService.sendPhotoMessage(chatId, file);
            file.delete();
        } else {
            telegramHttpService.sendTextMessage(chatId, "❌ Помилка скріншоту.");
        }
    }

    @Override
    public String getRequiredRole() { return "USER"; }
}