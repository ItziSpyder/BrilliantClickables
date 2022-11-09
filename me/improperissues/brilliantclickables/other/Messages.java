package me.improperissues.brilliantclickables.other;

import me.improperissues.brilliantclickables.BrilliantClickables;
import org.bukkit.event.Listener;

public class Messages implements Listener {

    public static BrilliantClickables plugin;
    public Messages(BrilliantClickables plugin) {
        Messages.plugin = plugin;
    }

    public static String starter = "§8[§7§lC§d+§8] §";
    public static String noperms = starter + "4You do not have access to this!";
}
