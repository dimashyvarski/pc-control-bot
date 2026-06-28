package com.desktopcontrol.pc_control_bot.service;

import com.desktopcontrol.pc_control_bot.command.CommandDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.List;

@Service
public class TelegramBotService implements SpringLongPollingBot, LongPollingUpdateConsumer {

    private final CommandDispatcher commandDispatcher;
    private final TelegramHttpService telegramHttpService;

    @Value("${spring.telegram.bot.token}")
    private String botToken;

    public TelegramBotService(CommandDispatcher commandDispatcher, TelegramHttpService telegramHttpService) {
        this.commandDispatcher = commandDispatcher;
        this.telegramHttpService = telegramHttpService;

        this.telegramHttpService.setBotMenuButton();
    }

    @Override
    public String getBotToken() { return botToken; }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() { return this; }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                commandDispatcher.dispatch(update.getMessage());
            }
            else if (update.hasCallbackQuery()) {
                commandDispatcher.dispatchCallback(update.getCallbackQuery());
            }
        }
    }
}