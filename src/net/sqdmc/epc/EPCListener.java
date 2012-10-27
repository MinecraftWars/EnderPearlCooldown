package net.sqdmc.epc;

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
        
    private Map<Player,Long> lastThrow = new HashMap<Player,Long>();
    
    
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerUseEP(PlayerInteractEvent e){   
    	if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK ) {
    		return;
    	}
    	
        Long now = System.currentTimeMillis();
        Player player = e.getPlayer();
        
        // may a player use pearls at all?
        if (!player.hasPermission("enderpearl.use")) {
        	if (EPC.EPC.showMessage)
        		player.sendMessage("You may not use ender pearls");
        	
        	e.setCancelled(true);
        	return;
        }
        
        // apply cooldown to player?
        if(e.getItem() != null && e.getItem().getType() == Material.ENDER_PEARL  && !validthrow(player, now)){
        	// Display message and cooldown time.
        	if (EPC.EPC.showMessage)
        		player.sendMessage(ChatColor.RED + "Enderpearl cooldown remaining: " + remainingCooldown(player, now) + " seconds.");
        	
        	e.setCancelled(true);
        }
    }
    
    /** Return remaining cooldown in seconds. */
    private long remainingCooldown(Player player, long throwTime) {
        Long lastPlayerPearl = lastThrow.get(player);
        return (EPC.EPC.cooldown - (throwTime - lastPlayerPearl)) / 1000;
    }
    
    /** Check if player is allowed to throw a pearl at this moment. */
    private boolean validthrow(Player player, long throwTime) {
    	if (!player.hasPermission("enderpearl.cooldown"))
    		return true; // no cooldown for this player
    	 
    	 
        Long lastPlayerPearl = lastThrow.get(player);

        // for players with cooldown, check if cooldown has passed
        if (lastPlayerPearl == null || (throwTime - lastPlayerPearl) >= EPC.EPC.cooldown) {
        	
            lastThrow.put(player, throwTime);
            return true;
        } 
        
        return false;
    }
}