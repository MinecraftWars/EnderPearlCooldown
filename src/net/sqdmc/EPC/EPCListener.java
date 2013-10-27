package net.sqdmc.epc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;

public class EPCListener implements Listener{

    private Map<String, Long> lastThrow = new HashMap<String, Long>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        checkPlayer(player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        checkPlayer(player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (lastThrow.containsKey(player.getName())) {
            lastThrow.remove(player.getName());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerUseEP(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK ) {
            return;
        }

        final Player player = e.getPlayer();

        // may a player use pearls at all?
        if (!player.hasPermission("enderpearl.use")) {
            if (EPC.EPC.showMessage) {
                player.sendMessage("You may not use ender pearls");
            }
            e.setCancelled(true);
            return;
        }

        // apply cooldown to a player
        final Long now = System.currentTimeMillis();
        if(e.getItem() != null && e.getItem().getType().equals(Material.ENDER_PEARL)  && !validthrow(player, now)) {
            // Display message and cooldown time.
            if (EPC.EPC.showMessage) {
                player.sendMessage(ChatColor.RED + "Enderpearl cooldown remaining: ~" + remainingCooldown(player.getName(), now) + " seconds.");
            }
            e.setCancelled(true);
        }
    }

    /** Return remaining cooldown in seconds. */
    private long remainingCooldown(String player, final long throwTime) {
        final Long lastPlayerPearl = lastThrow.get(player);
        return (EPC.EPC.cooldown - (throwTime - lastPlayerPearl)) / 1000;
    }

    /** Check if player is allowed to throw a pearl at this moment. */
    private boolean validthrow(Player player, long throwTime) {
        if (!player.hasPermission("enderpearl.cooldown")) {
            return true; // no cooldown for this player
        }

        Long lastPlayerPearl = lastThrow.get(player.getName());

        // for players with cooldown, check if cooldown has passed
        if (lastPlayerPearl == null || (throwTime - lastPlayerPearl) > EPC.EPC.cooldown) {
            lastThrow.put(player.getName(), throwTime);
            return true;
        }
        return false;
    }

    private void checkPlayer(String name) {
        if (lastThrow.containsKey(name)) {
            if (remainingCooldown(name, System.currentTimeMillis()) <= 0) {
                lastThrow.remove(name);
            }
        }
    }

}
