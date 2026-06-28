package com.desktopcontrol.pc_control_bot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    private Long telegramChatId;
    private String username;
    private String fullName;
    private boolean isAllowed;
    private String role;

    public User(Long telegramChatId, String username, String fullName, boolean isAllowed, String role) {
        this.telegramChatId = telegramChatId;
        this.username = username;
        this.fullName = fullName;
        this.isAllowed = isAllowed;
        this.role = role;
    }
    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAllowed() {
        return this.isAllowed;
    }
}
