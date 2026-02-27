package com.inferno.gui;

import com.inferno.Inferno;
import com.inferno.stats.PlayerStats;
import com.inferno.util.ColorUtil;
import com.inferno.util.ItemBuilder;
import com.inferno.util.SkullUtil;
import com.inferno.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StatsGUI {

    private final Inferno plugin;

    public StatsGUI(Inferno plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer, Player target) {
        PlayerStats stats = plugin.getStatsManager().getStats(target.getUniqueId());
        Inventory inv = Bukkit.createInventory(null, 27, ColorUtil.color("&6" + target.getName() + "'s Stats"));

        ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("&r").build();
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        // Player head in center
        inv.setItem(13, SkullUtil.getSkull(target.getUniqueId(),
                ColorUtil.color("&e" + target.getName())));

        // Stats items
        inv.setItem(10, new ItemBuilder(Material.EMERALD)
                .name("&aWins: &f" + stats.getWins())
                .lore("&7Total wins in duels")
                .build());
        inv.setItem(11, new ItemBuilder(Material.REDSTONE)
                .name("&cLosses: &f" + stats.getLosses())
                .lore("&7Total losses in duels")
                .build());
        inv.setItem(12, new ItemBuilder(Material.BLAZE_ROD)
                .name("&eWin Streak: &f" + stats.getWinStreak())
                .lore("&7Current win streak")
                .build());
        inv.setItem(14, new ItemBuilder(Material.NETHER_STAR)
                .name("&6Best Streak: &f" + stats.getBestStreak())
                .lore("&7Best win streak ever")
                .build());
        inv.setItem(15, new ItemBuilder(Material.GOLD_NUGGET)
                .name("&6Tickets Won: &f" + stats.getTotalBetWon())
                .lore("&7Total Duel Tickets won")
                .build());
        inv.setItem(16, new ItemBuilder(Material.BARRIER)
                .name("&cTickets Lost: &f" + stats.getTotalBetLost())
                .lore("&7Total Duel Tickets lost")
                .build());

        // Win rate
        inv.setItem(22, new ItemBuilder(Material.COMPASS)
                .name("&bWin Rate: &f" + String.format("%.1f%%", stats.getWinRate()))
                .lore("&7" + stats.getWins() + " W / " + stats.getLosses() + " L")
                .build());

        viewer.openInventory(inv);
    }
}
