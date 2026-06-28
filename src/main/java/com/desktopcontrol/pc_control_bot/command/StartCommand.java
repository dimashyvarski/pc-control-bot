package com.desktopcontrol.pc_control_bot.command;

import com.desktopcontrol.pc_control_bot.service.TelegramHttpService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public class StartCommand implements BotCommand {

    private final TelegramHttpService telegramHttpService;

    // Головне меню
    public static final String MAIN_MENU_JSON = "{"
            + "\"inline_keyboard\": ["
            + "  [{\"text\": \"📸 Зробити скріншот\", \"callback_data\": \"cmd_screenshot\"}],"
            + "  [{\"text\": \"🔒 Заблокувати ПК\", \"callback_data\": \"cmd_lock\"}],"
            + "  [{\"text\": \"🔊 Керування звуком...\", \"callback_data\": \"menu_volume\"}],"
            + "  [{\"text\": \"🧹 Очистити чат\", \"callback_data\": \"cmd_clear_chat\"}]"
            + "]"
            + "}";

    // Підменю звуку
    public static final String VOLUME_MENU_JSON = "{"
            + "\"inline_keyboard\": ["
            + "  ["
            + "    {\"text\": \"🔉 -20%\", \"callback_data\": \"cmd_v_down_20\"},"
            + "    {\"text\": \"🔉 -10%\", \"callback_data\": \"cmd_v_down_10\"},"
            + "    {\"text\": \"🔊 +10%\", \"callback_data\": \"cmd_v_up_10\"},"
            + "    {\"text\": \"🔊 +20%\", \"callback_data\": \"cmd_v_up_20\"}"
            + "  ],"
            + "  [{\"text\": \"🔇 Мут\", \"callback_data\": \"cmd_v_mute\"}],"
            + "  [{\"text\": \"◀️ Назад в меню\", \"callback_data\": \"menu_main\"}]"
            + "]"
            + "}";

    public StartCommand(TelegramHttpService telegramHttpService) {
        this.telegramHttpService = telegramHttpService;
    }

    @Override
    public boolean canExecute(String text) { return text.equals("/start"); }

    @Override
    public void execute(Message message) {
        Long chatId = message.getChatId();

        telegramHttpService.sendTextMessageWithKeyboard(chatId, "⚙️ Запуск панелі...", "{\"remove_keyboard\": true}");

        telegramHttpService.sendInlineMarkup(
                chatId,
                "🖥️ **Головна панель керування ПК**\n\nОберіть потрібну дію:",
                MAIN_MENU_JSON
        );
    }

    @Override
    public String getRequiredRole() { return "USER"; }
}