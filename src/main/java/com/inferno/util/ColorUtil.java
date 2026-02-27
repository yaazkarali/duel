package com.inferno.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ColorUtil {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private ColorUtil() {}

    /**
     * Translates a string with legacy color codes (&a, &#RRGGBB) into a Component.
     */
    public static Component color(String text) {
        if (text == null) return Component.empty();
        return LEGACY.deserialize(text);
    }

    /**
     * Translates a string with legacy color codes into a plain colored string.
     */
    public static String colorString(String text) {
        if (text == null) return "";
        return LegacyComponentSerializer.legacySection().serialize(color(text));
    }

    /**
     * Strip color codes from a string.
     */
    public static String strip(String text) {
        if (text == null) return "";
        return text.replaceAll("(&|§)[0-9a-fk-orA-FK-OR]", "")
                   .replaceAll("(&|§)#[0-9a-fA-F]{6}", "");
    }
}
