package com.inferno.duel;

import com.inferno.Inferno;
import com.inferno.api.events.DuelStartEvent;
import com.inferno.kit.KitApplier;
import com.inferno.util.SoundUtil;
import com.inferno.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class DuelCountdown extends BukkitRunnable {

    private final Inferno plugin;
    private final DuelSession session;
    private int secondsLeft;

    public DuelCountdown(Inferno plugin, DuelSession session) {
        this.plugin = plugin;
        this.session = session;
        this.secondsLeft = plugin.getConfigManager().getConfig().getInt("countdown.seconds", 5);
    }

    @Override
    public void run() {
        if (!session.getChallenger().isOnline() || !session.getOpponent().isOnline()) {
            cancel();
            plugin.getDuelManager().handleDisconnect(
                    session.getChallenger().isOnline() ? session.getOpponent() : session.getChallenger(),
                    session);
            return;
        }

        if (secondsLeft > 0) {
            String msg = TextUtil.replace(
                    plugin.getConfigManager().getMessageRaw("duel-countdown"),
                    "seconds", String.valueOf(secondsLeft));
            showTitle(msg, "&ePrepare to fight!");
            SoundUtil.play(session.getChallenger(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            SoundUtil.play(session.getOpponent(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            secondsLeft--;
        } else {
            // FIGHT!
            showTitle("&c&lFIGHT!", "");
            SoundUtil.play(session.getChallenger(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
            SoundUtil.play(session.getOpponent(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);

            session.setState(DuelState.ACTIVE);

            // Fire DuelStartEvent
            DuelStartEvent event = new DuelStartEvent(session);
            plugin.getServer().getPluginManager().callEvent(event);

            cancel();
        }
    }

    private void showTitle(String title, String subtitle) {
        Title titleObj = Title.title(
                TextUtil.component(title),
                TextUtil.component(subtitle),
                Title.Times.times(Duration.ofMillis(200), Duration.ofMillis(800), Duration.ofMillis(200))
        );
        session.getChallenger().showTitle(titleObj);
        session.getOpponent().showTitle(titleObj);
    }
}
