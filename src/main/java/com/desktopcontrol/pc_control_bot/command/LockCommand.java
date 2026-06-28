package com.desktopcontrol.pc_control_bot.command;

import com.desktopcontrol.pc_control_bot.service.ComputerControlService;
import com.desktopcontrol.pc_control_bot.service.TelegramHttpService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public class LockCommand implements BotCommand {

    private final ComputerControlService computerControlService;
    private final TelegramHttpService telegramHttpService;

    public LockCommand(ComputerControlService computerControlService, TelegramHttpService telegramHttpService) {
        this.computerControlService = computerControlService;
        this.telegramHttpService = telegramHttpService;
    }

    @Override
    public boolean canExecute(String text) {
        return text.equalsIgnoreCase("LOCK") || text.equalsIgnoreCase("Блок");
    }

    @Override
    public void execute(Message message) {
        telegramHttpService.sendTextMessage(message.getChatId(), "🔒 Блокую екран");
        computerControlService.lockComputer();
    }

    @Override
    public String getRequiredRole() { return "USER"; }
}