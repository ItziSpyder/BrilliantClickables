package me.improperissues.brilliantclickables.commands;

import me.improperissues.brilliantclickables.events.EntityClick;
import me.improperissues.brilliantclickables.file.ClickableMobs;
import me.improperissues.brilliantclickables.file.ExecuteBlocks;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;


public class Tabs implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        if (sender instanceof Player) {
            Player p = (Player) sender;
            Block targeted = p.getTargetBlockExact(5);

            switch (command.getName().toLowerCase().trim()) {
                case "clickableblock":
                    switch (args.length) {
                        case 1:
                            list.add("create");
                            list.add("delete");
                            list.add("teleport");
                            list.add("execute");
                            list.add("list");
                            list.add("command");
                            break;
                        case 2:
                            switch (args[0]) {
                                case "create":
                                    list.add("§8<name: string>");
                                    break;
                                case "teleport":
                                case "delete":
                                case "execute":
                                case "command":
                                    return ExecuteBlocks.getBlockNames();
                            }
                            break;
                        case 3:
                            switch (args[0]) {
                                case "create":
                                    for (World world : getServer().getWorlds()) {
                                        list.add(world.getName());
                                    }
                                    break;
                                case "command":
                                    list.add("add");
                                    list.add("remove");
                                    break;
                            }
                            break;
                        case 4:
                            switch (args[0]) {
                                case "create":
                                    if (targeted != null) {
                                        list.add(targeted.getLocation().getBlockX() + " " + targeted.getLocation().getBlockY() + " " + targeted.getLocation().getBlockZ());
                                    }
                                    break;
                                case "command":
                                    switch (args[2]) {
                                        case "add":
                                            list.add("§8<command: string>");
                                            break;
                                        case "remove":
                                            return ExecuteBlocks.getCommands(ExecuteBlocks.getBlockFromName(args[1]));
                                    }
                                    break;
                            }
                            break;
                        case 5:
                            switch (args[0]) {
                                case "create":
                                    if (targeted != null) {
                                        list.add(targeted.getLocation().getBlockY() + " " + targeted.getLocation().getBlockZ());
                                    }
                                    break;
                            }
                            break;
                        case 6:
                            switch (args[0]) {
                                case "create":
                                    if (targeted != null) {
                                        list.add(String.valueOf(targeted.getLocation().getBlockZ()));
                                    }
                                    break;
                            }
                            break;
                    }
                    break;
                case "clickablemob":
                    switch (args.length) {
                        case 1:
                            list.add("create");
                            list.add("delete");
                            list.add("teleport");
                            list.add("execute");
                            list.add("natural");
                            list.add("list");
                            list.add("command");
                            break;
                        case 2:
                            switch (args[0]) {
                                case "create":
                                    list.add("§8<name: string>");
                                    break;
                                case "teleport":
                                case "delete":
                                case "natural":
                                case "execute":
                                case "command":
                                    return ClickableMobs.getEntityNames();
                            }
                            break;
                        case 3:
                            switch (args[0]) {
                                case "create":
                                    return EntityClick.getEntityTypes();
                                case "command":
                                    list.add("add");
                                    list.add("remove");
                                    break;
                            }
                            break;
                        case 4:
                            switch (args[0]) {
                                case "create":
                                    list.add("§8<customname: string>");
                                    break;
                                case "command":
                                    switch (args[2]) {
                                        case "add":
                                            list.add("§8<command: string>");
                                            break;
                                        case "remove":
                                            return ClickableMobs.getCommands(ClickableMobs.getEntityFromName(args[1]));
                                    }
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }

        return list;
    }
}
