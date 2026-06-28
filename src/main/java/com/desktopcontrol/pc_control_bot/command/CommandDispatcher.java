package com.desktopcontrol.pc_control_bot.command;

import com.desktopcontrol.pc_control_bot.model.User;
import com.desktopcontrol.pc_control_bot.repository.UserRepository;
import com.desktopcontrol.pc_control_bot.service.TelegramHttpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import java.util.List;

@Component
public class CommandDispatcher {

    private final List<BotCommand> commands;
    private final UserRepository userRepository;
    private final TelegramHttpService telegramHttpService;

    @Value("${bot.owner-id}")
    @SuppressWarnings("SpringJavaInjectionPointsAuthenticationInspection")
    private Long ownerId;

    public CommandDispatcher(List<BotCommand> commands, UserRepository userRepository, TelegramHttpService telegramHttpService) {
        this.commands = commands;
        this.userRepository = userRepository;
        this.telegramHttpService = telegramHttpService;
    }

    public void dispatch(Message message) {
        String text = message.getText().trim();
        Long chatId = message.getChatId();

        User user = authenticate(message);
        if (!user.isAllowed()) {
            telegramHttpService.sendTextMessage(chatId, "❌ Доступ заборонено!");
            telegramHttpService.sendTextMessage(ownerId, "🔔 Спроба доступу від @" + message.getFrom().getUserName() + " (ID: " + chatId + ")");
            return;
        }

        for (BotCommand command : commands) {
            if (command.canExecute(text)) {
                // Перевірка Ролей
                if (command.getRequiredRole().equals("ADMIN") && !user.getRole().equals("ADMIN")) {
                    telegramHttpService.sendTextMessage(chatId, "🚫 Ця команда тільки для Адміна.");
                    return;
                }
                command.execute(message);
                return;
            }
        }
        telegramHttpService.sendTextMessage(chatId, "❓ Невідома команда.");
    }

    private User authenticate(Message message) {
        Long chatId = message.getChatId();
        if (chatId.equals(ownerId)) {
            return userRepository.findById(chatId).orElseGet(() ->
                    userRepository.save(new User(chatId, message.getFrom().getUserName(), message.getFrom().getFirstName(), true, "ADMIN"))
            );
        }
        return userRepository.findById(chatId).orElseGet(() ->
                userRepository.save(new User(chatId, message.getFrom().getUserName(), message.getFrom().getFirstName(), false, "USER"))
        );
    }
    public void dispatchCallback(org.telegram.telegrambots.meta.api.objects.CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();

        if (callbackQuery.getMessage() instanceof Message) {
            Message message = (Message) callbackQuery.getMessage();
            Long chatId = message.getChatId();
            Integer messageId = message.getMessageId();

            if (callbackData.equals("menu_volume")) {
                telegramHttpService.editInlineMarkup(chatId, messageId, StartCommand.VOLUME_MENU_JSON);
                return;
            }

            if (callbackData.equals("menu_main")) {
                telegramHttpService.editInlineMarkup(chatId, messageId, StartCommand.MAIN_MENU_JSON);
                return;
            }

            String textCommand = "";
            if (callbackData.equals("cmd_screenshot")) {
                textCommand = "SCREENSHOT";
            } else if (callbackData.equals("cmd_lock")) {
                textCommand = "LOCK";
            } else if (callbackData.equals("cmd_v_down_20")) {
                textCommand = "VOL_DOWN_20";
            } else if (callbackData.equals("cmd_v_down_10")) {
                textCommand = "VOL_DOWN_10";
            } else if (callbackData.equals("cmd_v_up_10")) {
                textCommand = "VOL_UP_10";
            } else if (callbackData.equals("cmd_v_up_20")) {
                textCommand = "VOL_UP_20";
            } else if (callbackData.equals("cmd_v_mute")) {
                textCommand = "VOL_MUTE";
            }
            if (!textCommand.isEmpty()) {
                message.setText(textCommand);
                this.dispatch(message);
            }
        } else {
            telegramHttpService.sendTextMessage(callbackQuery.getMessage().getChatId(), "❌ Помилка: повідомлення застаріло або недоступне.");
        }
    }
}