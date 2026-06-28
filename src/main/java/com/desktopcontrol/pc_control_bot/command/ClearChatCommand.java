package com.desktopcontrol.pc_control_bot.command;

import com.desktopcontrol.pc_control_bot.service.TelegramHttpService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public class ClearChatCommand implements BotCommand {

    private final TelegramHttpService telegramHttpService;

    public ClearChatCommand(TelegramHttpService telegramHttpService) {
        this.telegramHttpService = telegramHttpService;
    }

    @Override
    public boolean canExecute(String text) {
        return text.equals("CLEAR_CHAT");
    }

    @Override
    public void execute(Message message) {
        Long chatId = message.getChatId();
        Integer currentMessageId = message.getMessageId();
        telegramHttpService.clearChat(chatId, currentMessageId);
    }

    @Override
    public String getRequiredRole() {
        return "USER";
    }
}