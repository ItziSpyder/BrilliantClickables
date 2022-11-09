package me.improperissues.brilliantclickables.commands;

import me.improperissues.brilliantclickables.file.ClickableMobs;
import me.improperissues.brilliantclickables.file.ExecuteBlocks;
import me.improperissues.brilliantclickables.other.Messages;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        switch (command.getName().toLowerCase().trim()) {
            case "clickableblock":
                // /clickableblock create <name> <world> <x> <y> <z>

                if (args.length >= 6) {
                    String fileName = args[2] + "~" + args[3] + "~" + args[4] + "~" + args[5];
                    String name = args[1];
                    switch (args[0]) {
                        case "create":
                            if (ExecuteBlocks.getBlockList().contains(fileName)) {
                                sender.sendMessage(Messages.starter + "cA block already goes by this name!");
                                return false;
                            }
                            try {
                                World world = getServer().getWorld(args[2]);
                                double x = Double.parseDouble(args[3]);
                                double y = Double.parseDouble(args[4]);
                                double z = Double.parseDouble(args[5]);
                                Block block = world.getBlockAt((int) x, (int) y, (int) z);
                                ExecuteBlocks.createBlock(block,name);
                                sender.sendMessage(Messages.starter + "7Created a new block \"§f" + name + "\" §7in world " +
                                        "§f\"" + world.getName() + "\" §7; Coordinates are §8[§f" + x + "§7,§f" + y + "§7,§f" + z + "§8]");
                                return true;
                            } catch (IllegalArgumentException | NullPointerException exception) {
                                return false;
                            }
                    }
                } else if (args.length == 2) {
                    String name = args[1];
                    switch (args[0]) {
                        case "delete":
                            if (ExecuteBlocks.getBlockNames().contains(name)) {
                                Block block = ExecuteBlocks.getBlockFromName(name);
                                File file = ExecuteBlocks.getFile(block);
                                file.delete();
                                sender.sendMessage(Messages.starter + "7Deleted block §f\"" + name + "\" §7!");
                                return true;
                            } else {
                                sender.sendMessage(Messages.starter + "cFile does not exist!");
                            }
                            break;
                        case "teleport":
                            if (ExecuteBlocks.getBlockNames().contains(name)) {
                                if (!(sender instanceof Player)) {
                                    return false;
                                }
                                Player p = (Player) sender;
                                Block block = ExecuteBlocks.getBlockFromName(name);
                                p.teleport(block.getLocation());
                                sender.sendMessage(Messages.starter + "7Teleported to block §f\"" + name + "\" §7!");
                                return true;
                            } else {
                                sender.sendMessage(Messages.starter + "cLocation does not exist!");
                            }
                            break;
                        case "execute":
                            if (ExecuteBlocks.getBlockNames().contains(name)) {
                                Block block = ExecuteBlocks.getBlockFromName(name);
                                List<String> commands = ExecuteBlocks.getCommands(block);
                                ExecuteBlocks.executeAll(sender,block);
                                sender.sendMessage(Messages.starter + "7Executed §f" + commands.size() + " §7command(s) from block §f\"" + name + "\" §7!");
                                return true;
                            } else {
                                sender.sendMessage(Messages.starter + "cLocation does not exist!");
                            }
                            break;
                    }
                } else if (args.length == 1) {
                    switch (args[0]) {
                        case "list":
                            List<String> blocks = ExecuteBlocks.getBlockNames();
                            StringBuilder message = new StringBuilder(("\n§fThere are (§a" + blocks.size() + "§f) clickable blocks in total: §7" + blocks + "\n"));
                            for (World world : getServer().getWorlds()) {
                                List<String> worldBlocks = ExecuteBlocks.getWorldBlockNames(world);
                                message.append("\n§fThere are (§a").append(worldBlocks.size()).append("§f) clickable blocks in §a").append(world.getName()).append(": §7").append(worldBlocks);
                            }
                            sender.sendMessage(String.valueOf(message.append("\n ")));
                            return true;
                    }
                }
                if (args.length >= 4) {
                    String name = args[1];
                    switch (args[0]) {
                        case "command":
                            switch (args[2]) {
                                case "add":
                                    if (ExecuteBlocks.getBlockNames().contains(name)) {
                                        Block block = ExecuteBlocks.getBlockFromName(name);
                                        List<String> commands = ExecuteBlocks.getCommands(block);
                                        StringBuilder builder = new StringBuilder();
                                        for (int i = 3; i < args.length; i ++) {
                                            builder.append(args[i]).append(" ");
                                        }
                                        commands.add(String.valueOf(builder).trim());
                                        ExecuteBlocks.setCommands(commands,name);
                                        sender.sendMessage(Messages.starter + "7Added §f1 §7command(s) block §f\"" + name + "\" §7, now §f" + commands.size() + " §7!");
                                        return true;
                                    } else {
                                        sender.sendMessage(Messages.starter + "cFile does not exist!");
                                    }
                                    break;
                                case "remove":
                                    if (ExecuteBlocks.getBlockNames().contains(name)) {
                                        Block block = ExecuteBlocks.getBlockFromName(name);
                                        List<String> commands = ExecuteBlocks.getCommands(block);
                                        StringBuilder builder = new StringBuilder();
                                        for (int i = 3; i < args.length; i ++) {
                                            builder.append(args[i]).append(" ");
                                        }
                                        commands.remove(String.valueOf(builder).trim());
                                        ExecuteBlocks.setCommands(commands,name);
                                        sender.sendMessage(Messages.starter + "7Removed §f1 §7command(s) block §f\"" + name + "\" §7, now §f" + commands.size() + " §7!");
                                        return true;
                                    } else {
                                        sender.sendMessage(Messages.starter + "cFile does not exist!");
                                    }
                                    break;
                            }
                    }
                }
                break;
            case "clickablemob":
                // /clickablemob create <name> <entitytype> <customname>

                if (args.length >= 4) {
                    String name = args[1];
                    String customName = args[3].replaceAll("&","§").replaceAll("_"," ");
                    switch (args[0]) {
                        case "create":
                            try {
                                if (!(sender instanceof Player)) {
                                    return false;
                                }
                                Player p = (Player) sender;
                                EntityType type = EntityType.valueOf(args[2].toUpperCase().trim());
                                Entity entity = p.getWorld().spawnEntity(p.getLocation(),type,false);
                                if (!(entity instanceof LivingEntity)) {
                                    entity.remove();
                                    sender.sendMessage(Messages.starter + "cPlease use living entities!");
                                    return false;
                                }
                                if (ClickableMobs.getEntityNames().contains(name)) {
                                    entity.remove();
                                    sender.sendMessage(Messages.starter + "cThis file already exists!");
                                    return false;
                                }
                                ClickableMobs.createMob(entity,name);
                                entity.setCustomName(customName);
                                entity.setCustomNameVisible(true);
                                entity.setInvulnerable(true);
                                entity.setSilent(true);
                                entity.setGravity(false);
                                ((LivingEntity) entity).setCanPickupItems(false);
                                ((LivingEntity) entity).setCollidable(false);
                                ((LivingEntity) entity).setAI(false);
                                ((LivingEntity) entity).setRemoveWhenFarAway(false);
                                sender.sendMessage(Messages.starter + "7Created clickable mob §f\"" + name + "\"§7, type §f\"" + type.name().toLowerCase() + "\"" +
                                        "§7, name §f\"" + entity.getCustomName() + "§f\" §7!");
                                return true;
                            } catch (IllegalArgumentException exception) {
                                sender.sendMessage(Messages.starter + "cThat is not a valid entity!");
                            }
                            break;
                    }
                } else if (args.length == 2) {
                    String name = args[1];
                    switch (args[0]) {
                        case "delete":
                            if (ClickableMobs.getEntityNames().contains(name)) {
                                Entity entity = ClickableMobs.getEntityFromName(name);
                                File file = ClickableMobs.getFile(entity);
                                file.delete();
                                entity.remove();
                                sender.sendMessage(Messages.starter + "7Deleted entity §f\"" + name + "\" §7!");
                                return true;
                            } else {
                                sender.sendMessage(Messages.starter + "cFile does not exist!");
                            }
                            break;
                        case "teleport":
                            if (ClickableMobs.getEntityNames().contains(name)) {
                                if (!(sender instanceof Player)) {
                                    return false;
                                }
                                Player p = (Player) sender;
                                Entity entity = ClickableMobs.getEntityFromName(name);
                                p.teleport(entity.getLocation());
                                sender.sendMessage(Messages.starter + "7Teleported to entity §f\"" + name + "\" §7!");
                                return true;
                            } else {
                                sender.sendMessage(Messages.starter + "cEntity does not exist!");
                            }
                            break;
                        case "execute":
                            if (ClickableMobs.getEntityNames().contains(name)) {
                                Entity entity = ClickableMobs.getEntityFromName(name);
                                List<String> commands = ClickableMobs.getCommands(entity);
                                ClickableMobs.executeCommands(sender,entity);
                                sender.sendMessage(Messages.starter + "7Executed §f" + commands.size() + " §7command(s) from entity §f\"" + name + "\" §7!");
                                return true;
                            } else {
                                sender.sendMessage(Messages.starter + "cEntity does not exist!");
                            }
                            break;
                        case "natural":
                            if (ClickableMobs.getEntityNames().contains(name)) {
                                Entity entity = ClickableMobs.getEntityFromName(name);
                                boolean bool = true;
                                if (ClickableMobs.isResponsive(entity)) {
                                    ClickableMobs.setResponsive(entity,false);
                                    bool = false;
                                } else {
                                    ClickableMobs.setResponsive(entity,true);
                                }
                                sender.sendMessage(Messages.starter + "7Set §f\"" + name + "\" §7natural behaviour to §f" + bool  + " §7!");
                                return true;
                            } else {
                                sender.sendMessage(Messages.starter + "cEntity does not exist!");
                            }
                            break;
                    }
                } else if (args.length == 1) {
                    switch (args[0]) {
                        case "list":
                            List<String> entities = ClickableMobs.getEntityNames();
                            sender.sendMessage("\n§fThere are (§a" + entities.size() + "§f) clickable mobs in total: §7" + entities + "\n ");
                            return true;
                    }
                }
                if (args.length >= 4) {
                    String name = args[1];
                    switch (args[0]) {
                        case "command":
                            switch (args[2]) {
                                case "add":
                                    if (ClickableMobs.getEntityNames().contains(name)) {
                                        Entity entity = ClickableMobs.getEntityFromName(name);
                                        List<String> commands = ClickableMobs.getCommands(entity);
                                        StringBuilder builder = new StringBuilder();
                                        for (int i = 3; i < args.length; i ++) {
                                            builder.append(args[i]).append(" ");
                                        }
                                        commands.add(String.valueOf(builder).trim());
                                        ClickableMobs.setCommands(commands,name);
                                        sender.sendMessage(Messages.starter + "7Added §f1 §7command(s) entity §f\"" + name + "\" §7, now §f" + commands.size() + " §7!");
                                        return true;
                                    } else {
                                        sender.sendMessage(Messages.starter + "cFile does not exist!");
                                    }
                                    break;
                                case "remove":
                                    if (ClickableMobs.getEntityNames().contains(name)) {
                                        Entity entity = ClickableMobs.getEntityFromName(name);
                                        List<String> commands = ClickableMobs.getCommands(entity);
                                        StringBuilder builder = new StringBuilder();
                                        for (int i = 3; i < args.length; i ++) {
                                            builder.append(args[i]).append(" ");
                                        }
                                        commands.remove(String.valueOf(builder).trim());
                                        ClickableMobs.setCommands(commands,name);
                                        sender.sendMessage(Messages.starter + "7Removed §f1 §7command(s) entity §f\"" + name + "\" §7, now §f" + commands.size() + " §7!");
                                        return true;
                                    } else {
                                        sender.sendMessage(Messages.starter + "cFile does not exist!");
                                    }
                                    break;
                            }
                    }
                }
                break;
        }

        return false;
    }
}
