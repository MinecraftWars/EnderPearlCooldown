package net.sqdmc.EPC;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class EPCListener implements Listener{
        
    private long cooldown = 15*1000; //15s
    private Map<Player,Long> lastThrow = new HashMap<Player,Long>();
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerUseEP(PlayerInteractEvent e){   
    	if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK ) {
    		return;
    	}
    	
        Long now = System.currentTimeMillis();
        Player player = e.getPlayer();
                
        if(e.getItem() != null && e.getItem().getType() == Material.ENDER_PEARL  && !validthrow(player, now)){
        	// Display message and cooldown time.
        	player.sendMessage(ChatColor.RED + "Teleportation with Enderpearls needs to cooldown! " + remainingCooldown(player, now) + " Seconds.");
        	e.setCancelled(true);
        }
    }
    
    /** Return remaining cooldown in seconds. */
    private long remainingCooldown(Player player, long throwTime) {
        Long lastPlayerPearl = lastThrow.get(player);
        return (cooldown - (throwTime - lastPlayerPearl)) / 1000;
    }
    
    private boolean validthrow(Player player, long throwTime) {
        Long lastPlayerPearl = lastThrow.get(player);

        if (lastPlayerPearl == null || (throwTime - lastPlayerPearl) >= cooldown) {
            lastThrow.put(player, throwTime);
            return true;
        } else { return false; }
    }
}