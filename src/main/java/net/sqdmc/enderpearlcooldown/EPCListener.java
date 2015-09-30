package net.sqdmc.enderpearlcooldown;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

import static org.bukkit.Material.*;

public class EPCListener implements Listener {

    /** playername -> last throw timestamp */
    private final Map<String, Long> lastThrow = new HashMap<String, Long>();

    // materials that allow interactions are ignored, since the enderpearl will not fire
    private final static Set<Material> interactables =
            new HashSet<>(Arrays.asList(
                ANVIL, COMMAND, BED, BEACON, BED_BLOCK, BREWING_STAND, BURNING_FURNACE, CAKE_BLOCK, CHEST,
                DIODE, DIODE_BLOCK_OFF, DIODE_BLOCK_ON, DISPENSER, DROPPER, ENCHANTMENT_TABLE, ENDER_CHEST, FENCE_GATE,
                FENCE_GATE, FURNACE, HOPPER, IRON_DOOR, IRON_DOOR_BLOCK, ITEM_FRAME, LEVER,  REDSTONE_COMPARATOR,
                REDSTONE_COMPARATOR_OFF, REDSTONE_COMPARATOR_ON, STONE_BUTTON, TRAP_DOOR, TRAPPED_CHEST,
                WOODEN_DOOR, WOOD_BUTTON, WOOD_DOOR, WORKBENCH
            ));

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerUseEP(PlayerInteractEvent event) {

        if (event.isCancelled()) return;

        if (event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK
                || event.getItem() == null
                || event.getItem().getType() != ENDER_PEARL) {
            return;
        }
        // ignore certain materials...
        if (event.getClickedBlock() != null && !event.getPlayer().isSneaking()) {
            Material clickedMat = event.getClickedBlock().getType();
            if (interactables.contains(clickedMat)) return;
        }

        Player player = event.getPlayer();

        // may a player use pearls at all?
        if (!player.hasPermission("enderpearl.use")) {
            sendMessageChecked(player, EPC.getInstance().messageNotAllowed);
            event.setCancelled(true);
            return;
        }

        // apply cooldown to player
        Long now = System.currentTimeMillis();
        if (validthrow(player, now)) {
            if (!pay(player)) { // player can't pay
                event.setCancelled(true);
            } else { // allow throw, set cooldown
                lastThrow.put(player.getName(), now);
            }
        } else {
            event.setCancelled(true);
        }
    }

    /** Check if player needs to and can pay for a throw. */
    private boolean pay(Player player) {
        if (!player.hasPermission("enderpearl.pay")
                || EPC.getInstance().price == 0
                || EPC.getInstance().economy == null) {
            return true;
        }

        double price = EPC.getInstance().price;
        boolean success = false;
        if (EPC.getInstance().economy.has(player, price)) {
            success = EPC.getInstance().economy.withdrawPlayer(player, price)
                    .transactionSuccess();
        }

        if (!success) {
            sendMessageChecked(
                    player,
                    EPC.getInstance().messageMoney.replace("{price}",
                    EPC.getInstance().economy.format(price)));
        }
        return success;
    }

    /** Return remaining cooldown in seconds. */
    private double remainingCooldown(Player player, long throwTime) {
        Long lastPlayerPearl = lastThrow.get(player.getName());
        return (EPC.getInstance().cooldown - (throwTime - lastPlayerPearl)) / 1000.0;
    }

    /** Check if player is allowed to throw a pearl at this moment. */
    private boolean validthrow(Player player, long throwTime) {
        if (!player.hasPermission("enderpearl.cooldown")) {
            return true; // no cooldown for this player
        }

        if (player.getGameMode() == GameMode.CREATIVE)
            return false;

        Long lastPlayerPearl = lastThrow.get(player.getName());

        // for players with cooldown, check if cooldown has passed
        if (lastPlayerPearl == null
                || (throwTime - lastPlayerPearl) >= EPC.getInstance().cooldown) {
            return true;
        }

        sendMessageChecked(
                player,
                EPC.getInstance().messageCooldown.replace(
                        "{seconds}",
                        String.format("%.1f",
                                remainingCooldown(player, throwTime))));
        return false;
    }

    private static void sendMessageChecked(Player player, String message) {
        if (EPC.getInstance().showMessage) {
            player.sendMessage(message);
        }
    }
}