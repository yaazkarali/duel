package com.inferno.ticket;

import com.inferno.Inferno;
import com.inferno.util.TextUtil;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TicketVendorListener implements Listener {

    private final Inferno plugin;

    public TicketVendorListener(Inferno plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle Citizens NPC right-click to open the ticket shop.
     * Only registered if Citizens is present.
     */
    @EventHandler
    public void onNPCClick(NPCRightClickEvent event) {
        String vendorName = plugin.getConfigManager().getConfig()
                .getString("ticket.vendor-npc-name", "Duel Vendor");
        if (!event.getNPC().getName().equalsIgnoreCase(vendorName)) return;

        if (!(event.getClicker() instanceof org.bukkit.entity.Player player)) return;
        plugin.getTicketShopGUI().open(player);
    }
}
