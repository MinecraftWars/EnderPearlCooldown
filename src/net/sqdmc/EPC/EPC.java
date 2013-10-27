package net.sqdmc.epc;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EPC extends JavaPlugin {

    public static EPC EPC;
    @SuppressWarnings("unused")
    private Logger log;

    // configuration section
    /** Cooldown of pearls in ms. */
    long cooldown = 3000;

    /** Show cooldown message. */
    boolean showMessage = true;

    public void onEnable() {
        log = getLogger();
        EPC = this;

        saveDefaultConfig();
        Configuration config = getConfig();
        cooldown = config.getLong("cooldown");
        showMessage = config.getBoolean("showMessage");

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EPCListener(), this);

        getCommand("epc").setExecutor(new Commands());
    }

    private class Commands implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
            if (args.length >=1 && "reload".equalsIgnoreCase(args[0])) {
                reloadConfig();
                Configuration config = getConfig();
                cooldown = config.getLong("cooldown");
                showMessage = config.getBoolean("showMessage");
                sender.sendMessage("[EPC] Reloaded configuration!");
                return true;
            }
            return false;
        }
    }

}
