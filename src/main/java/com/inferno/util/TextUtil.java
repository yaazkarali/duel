package com.inferno.util;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Map;

public final class TextUtil {

    private TextUtil() {}

    /**
     * Replace placeholders in text with values from a map.
     * Keys should be the placeholder names (without braces), e.g. "player".
     */
    public static String replace(String text, Map<String, String> placeholders) {
        if (text == null) return "";
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    /**
     * Replace a single placeholder in text.
     */
    public static String replace(String text, String key, String value) {
        if (text == null) return "";
        return text.replace("{" + key + "}", value);
    }

    /**
     * Send a colored message to a player.
     */
    public static void send(Player player, String message) {
        player.sendMessage(ColorUtil.color(message));
    }

    /**
     * Send a component message to a player.
     */
    public static void send(Player player, Component component) {
        player.sendMessage(component);
    }

    /**
     * Build a Component from a colored string.
     */
    public static Component component(String text) {
        return ColorUtil.color(text);
    }
}
