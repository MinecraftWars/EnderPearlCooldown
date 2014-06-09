package net.sqdmc.enderpearlcooldown;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import java.io.IOException;
import java.util.logging.Logger;

public class EPC extends JavaPlugin {

    private static EPC instance;
    private Logger log;

    // configuration section

    /** Cooldown of pearls in ms. */
    public long cooldown = 3000;

    /** Price of a pearl throw. */
    public double price = 0;

    /** Show cooldown messages. */
    public boolean showMessage = true;

    /** Message to show on insufficient funds to cast enderpearl. */
    public String messageMoney = "§cNot enough money to throw pearl. Need at least {price}.";
    /** Message to show on cooldown. */
    public String messageCooldown = "§cEnderpearl cooldown remaining: {seconds} seconds.";
    /** Message to send if player is not allowed to use ender pearls. */
    public String messageNotAllowed = "§cYou may not use ender pearls.";

    /** Vault hook. */
    Economy economy = null;

    public void onEnable() {
        log = getLogger();
        instance = this;

        saveDefaultConfig();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EPCListener(), this);

        getCommand("epc").setExecutor(this);

        setupEconomy();
        reloadConfig();
        startMetrics();
    }

    private boolean setupEconomy() {
        try {
            @SuppressWarnings("unchecked")
            Class<Economy> c = (Class<Economy>) Class
                    .forName("net.milkbowl.vault.economy.Economy");
            economy = getServer().getServicesManager().load(c);

        } catch (ClassNotFoundException e) {
            // loading vault failed
        }

        if (economy != null) {
            log.info("Vault hooked. Enderpearl throwing price enabled.");
        } else {
            log.info("Unable to load Vault. Enderpearl throwing price is disabled.");
            price = 0;
        }

        return (economy != null);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        Configuration config = getConfig();
        cooldown = config.getLong("cooldown");
        price = config.getDouble("price");
        showMessage = config.getBoolean("showMessage");

        ConfigurationSection msgs = config.getConfigurationSection("messages");
        messageCooldown = msgs.getString("cooldown", messageCooldown);
        messageMoney = msgs.getString("money", messageMoney);
        messageNotAllowed = msgs.getString("notallowed", messageNotAllowed);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
            String commandLabel, String[] args) {

        if (args.length >= 1 && "reload".equalsIgnoreCase(args[0])) {
            reloadConfig();
            sender.sendMessage("[EPC] Reloaded configuration!");
            return true;
        }

        return false;
    }

    private void startMetrics() {
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException ex) {
            getLogger().warning("Failed to load metrics :(");
        }
    }

    public static EPC getIstance() {
        return instance;
    }

}
