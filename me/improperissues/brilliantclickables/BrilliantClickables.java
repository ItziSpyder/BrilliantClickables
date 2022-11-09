package me.improperissues.brilliantclickables;

import me.improperissues.brilliantclickables.commands.Commands;
import me.improperissues.brilliantclickables.commands.Tabs;
import me.improperissues.brilliantclickables.events.BlockClick;
import me.improperissues.brilliantclickables.events.EntityClick;
import me.improperissues.brilliantclickables.other.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BrilliantClickables extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getLogger().info("BrilliantClickables loaded");

        // Files
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Events
        getServer().getPluginManager().registerEvents(new Messages(this), this);
        getServer().getPluginManager().registerEvents(new BlockClick(), this);
        getServer().getPluginManager().registerEvents(new EntityClick(), this);

        // Command
        getCommand("clickableblock").setExecutor(new Commands());
        getCommand("clickableblock").setTabCompleter(new Tabs());
        getCommand("clickablemob").setExecutor(new Commands());
        getCommand("clickablemob").setTabCompleter(new Tabs());

        // Items

        // Loops
        EntityClick.loopLookAtNearestPlayer();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getLogger().info("BrilliantClickables unloaded");
    }

    public static void shutdown() {
        Bukkit.getServer().getLogger().warning("BrilliantClickables shutting down!");
        PluginManager pm = Bukkit.getServer().getPluginManager();
        Plugin pl = pm.getPlugin("BrilliantClickables");
        assert pl != null;
        pm.disablePlugin(pl);
    }
}
