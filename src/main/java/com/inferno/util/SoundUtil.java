package com.inferno.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundUtil {

    private SoundUtil() {}

    public static void play(Player player, Sound sound) {
        play(player, sound, 1.0f, 1.0f);
    }

    public static void play(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void play(Player player, String soundName) {
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase().replace(".", "_"));
            play(player, sound);
        } catch (IllegalArgumentException e) {
            // Unknown sound, ignore
        }
    }
}
