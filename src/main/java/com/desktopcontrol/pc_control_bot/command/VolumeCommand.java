package com.desktopcontrol.pc_control_bot.command;

import com.desktopcontrol.pc_control_bot.service.ComputerControlService;
import com.desktopcontrol.pc_control_bot.service.TelegramHttpService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public class VolumeCommand implements BotCommand {

    private final ComputerControlService computerControlService;
    private final TelegramHttpService telegramHttpService;

    public VolumeCommand(ComputerControlService computerControlService, TelegramHttpService telegramHttpService) {
        this.computerControlService = computerControlService;
        this.telegramHttpService = telegramHttpService;
    }

    @Override
    public boolean canExecute(String text) {
        return text.startsWith("VOL_");
    }

    @Override
    public void execute(Message message) {
        String text = message.getText().trim();
        Long chatId = message.getChatId();

        switch (text) {
            case "VOL_UP_10":
                computerControlService.changeVolume(5, true);
                telegramHttpService.sendTextMessage(chatId, "🔊 Гучність збільшено на 10%");
                break;
            case "VOL_UP_20":
                computerControlService.changeVolume(10, true);
                telegramHttpService.sendTextMessage(chatId, "🔊 Гучність збільшено на 20%");
                break;
            case "VOL_DOWN_10":
                computerControlService.changeVolume(5, false);
                telegramHttpService.sendTextMessage(chatId, "🔉 Гучність зменшено на 10%");
                break;
            case "VOL_DOWN_20":
                computerControlService.changeVolume(10, false);
                telegramHttpService.sendTextMessage(chatId, "🔉 Гучність зменшено на 20%");
                break;
            case "VOL_MUTE":
                computerControlService.toggleMute();
                telegramHttpService.sendTextMessage(chatId, "🔇 Режим без звуку (Mute) перемикнуто");
                break;
        }
    }

    @Override
    public String getRequiredRole() { return "USER"; }
}