package net.sqdmc.EPC;
 
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
 
public class EPC extends JavaPlugin {

        public void onDisable() {
        	System.out.println("[EPC] Deactive.");      
        }

        public void onEnable() {
    		PluginManager pm = getServer().getPluginManager();
    		pm.registerEvents(new EPCListener(), this);
        	System.out.println("[EPC] Active.");  
        }
}
