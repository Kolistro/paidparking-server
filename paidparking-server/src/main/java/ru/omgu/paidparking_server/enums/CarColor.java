package ru.omgu.paidparking_server.enums;

import lombok.Getter;

@Getter
public enum CarColor {
    WHITE("#FFFFFF"),
    BLACK("#000000"),
    GRAY("#808080"),
    SILVER("#C0C0C0"),
    RED("#FF0000"),
    BLUE("#0000FF"),
    GREEN("#008000"),
    YELLOW("#FFFF00"),
    ORANGE("#FFA500"),
    BROWN("#A52A2A"),
    PURPLE("#800080"),
    GOLD("#FFD700"),
    BEIGE("#F5F5DC"),
    UNDEFINED(null);

    private final String hexCode;

    CarColor(String hexCode) {
        this.hexCode = hexCode;
    }
}
