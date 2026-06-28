package com.desktopcontrol.pc_control_bot.service;

import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ComputerControlService {

    // Скрін
    public File makeScreenshot() {
        try {
            System.setProperty("java.awt.headless", "false");
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage capture = new Robot().createScreenCapture(screenRect);

            File imageFile = new File("screenshot_" + UUID.randomUUID() + ".png");
            ImageIO.write(capture, "png", imageFile);
            return imageFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Блок
    public void lockComputer() {
        try {
            new ProcessBuilder("cmd", "/c", "rundll32.exe user32.dll,LockWorkStation").start();
        } catch (IOException e) {
            System.err.println("Помилка блокування: " + e.getMessage());
        }
    }

    //Звук
    public void changeVolume(int steps, boolean up) {
        try {
            new ProcessBuilder("cmd", "/c", "start sndvol.exe").start();
            Thread.sleep(250);

            String key = up ? "{UP}" : "{DOWN}";
            StringBuilder volumeCommands = new StringBuilder();
            for (int i = 0; i < steps; i++) {
                volumeCommands.append("$wsh.SendKeys('").append(key).append("'); ");
            }

            run("powershell -Command \"$wsh = New-Object -ComObject WScript.Shell; " + volumeCommands.toString() + "\"");

            Thread.sleep(100);
            new ProcessBuilder("cmd", "/c", "taskkill /f /im sndvol.exe").start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Мут
    public void toggleMute() {
        run("powershell -Command \"(New-Object -ComObject WScript.Shell).SendKeys([char]173)\"");
    }

    private void run(String cmd) {
        try {
            new ProcessBuilder("cmd", "/c", cmd).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}