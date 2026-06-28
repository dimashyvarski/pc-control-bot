package com.desktopcontrol.pc_control_bot.command;

import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface BotCommand {
    boolean canExecute(String text);

    void execute(Message message);

    String getRequiredRole();
}