package me.improperissues.brilliantclickables.file;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ExecuteBlocks {

    public static final File dataFolder = getServer().getPluginManager().getPlugin("BrilliantClickables").getDataFolder();
    private static File file;
    private static FileConfiguration data;

    public static void createBlock(Block block, String name) {
        // setup
        Location location = block.getLocation();
        file = new File(dataFolder,"executables/" + location.getWorld().getName() + "/" + location.getWorld().getName() + "~" + location.getBlockX() + "~"
                + location.getBlockY() + "~" + location.getBlockZ() + ".yml");
        data = YamlConfiguration.loadConfiguration(file);
        save(file,data);

        // info fill-in
        data.set("clickableblock.name",name);
        data.set("clickableblock.location",location);
        data.set("clickableblock.command",new ArrayList<>(Arrays.asList("tellraw @s {\"text\":\"Set a command for me! /clickableblock command add <command>\"}")));
        save(file,data);
    }

    public static void setCommands(List<String> commands, String name) {
        Block block = getBlockFromName(name);
        file = getFile(block);
        data = YamlConfiguration.loadConfiguration(file);
        data.set("clickableblock.command",commands);
        save(file,data);
    }

    public static void save(File file, FileConfiguration data) {
        try {
            data.save(file);
        } catch (IOException exception) {
            getServer().getLogger().warning("Can not save file " + file.getName() + " !");
        }
    }

    public static File getFile(Block block) {
        Location location = block.getLocation();
        String path = "executables/" + location.getWorld().getName() + "/" + location.getWorld().getName() + "~" + location.getBlockX() + "~"
                + location.getBlockY() + "~" + location.getBlockZ() + ".yml";
        return new File(dataFolder,path);
    }

    public static List<String> getCommands(Block block) {
        if (block != null) {
            file = getFile(block);
            data = YamlConfiguration.loadConfiguration(file);
            return data.getStringList("clickableblock.command");
        } else {
            return new ArrayList<>();
        }
    }

    public static String getName(Block block) {
        file = getFile(block);
        data = YamlConfiguration.loadConfiguration(file);
        if (file.exists()) {
            return data.getString("clickableblock.name");
        }
        return "";
    }

    public static Block getBlock(String name) {
        String[] info = name.split("~");
        World world = getServer().getWorld(info[0]);
        assert world != null;
        return world.getBlockAt(Integer.parseInt(info[1]),Integer.parseInt(info[2]),Integer.parseInt(info[3]));
    }

    public static List<String> getWorldBlockList(World world) {
        List<String> list = new ArrayList<>();
        try {
            File[] blockFiles = new File(dataFolder,"executables/" + world.getName() + "/").listFiles();
            for (File block : blockFiles) {
                list.add(getFileName(block));
            }
        } catch (NullPointerException exception) {
            // empty
        }
        return list;
    }

    public static List<String> getWorldBlockNames(World world) {
        List<String> list = new ArrayList<>();
        try {
            File[] blockFiles = new File(dataFolder,"executables/" + world.getName() + "/").listFiles();
            for (File blockFile : blockFiles) {
                Block block = getBlock(getFileName(blockFile));
                list.add(getName(block));
            }
        } catch (NullPointerException exception) {
            // empty
        }
        return list;
    }

    public static List<String> getBlockList() {
        List<String> list = new ArrayList<>();
        for (World world : getServer().getWorlds()) {
            list.addAll(getWorldBlockList(world));
        }
        return list;
    }

    public static HashMap<String,String> getBlockMap() {
        HashMap<String,String> map = new HashMap<>();
        for (String file : getBlockList()) {
            Block block = getBlock(file);
            map.put(file,getName(block));
        }
        return map;
    }

    public static List<String> getBlockNames() {
        return new ArrayList<>(getBlockMap().values());
    }

    public static Block getBlockFromName(String name) {
        for (Map.Entry<String,String> key : getBlockMap().entrySet()) {
            if (Objects.equals(key.getValue(), name)) {
                return getBlock(key.getKey());
            }
        }
        return null;
    }

    public static void executeAll(CommandSender sender, Block block) {
        for (String command : getCommands(block)) {
            getServer().dispatchCommand(sender,command);
        }
    }

    public static String getFileName(File file) {
        return file.getName().substring(0,file.getName().length() - 4);
    }
}
