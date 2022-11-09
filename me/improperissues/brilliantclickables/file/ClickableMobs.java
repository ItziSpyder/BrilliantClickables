package me.improperissues.brilliantclickables.file;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ClickableMobs {

    private static final File dataFolder = ExecuteBlocks.dataFolder;
    private static File file;
    private static FileConfiguration data;

    public static void createMob(Entity entity, String name) {
        // setup
        UUID id = entity.getUniqueId();
        file = new File(dataFolder,"clickablemobs/" + id + ".yml");
        data = YamlConfiguration.loadConfiguration(file);
        save(file,data);

        // fill-in
        data.set("clickablemob.name",name);
        data.set("clickablemob.responsive",true);
        data.set("clickablemob.command",new ArrayList<>(Arrays.asList("tellraw @s {\"text\":\"Set a command for me! /clickablemob command add <command>\"}")));
        save(file,data);
    }

    public static void save(File file, FileConfiguration data) {
        try {
            data.save(file);
        } catch (IOException exception) {
            getServer().getLogger().warning("Can not save file " + file.getName() + " !");
        }
    }

    public static List<String> getCommands(Entity entity) {
        if (entity != null) {
            file = getFile(entity);
            data = YamlConfiguration.loadConfiguration(file);
            return data.getStringList("clickablemob.command");
        } else {
            return new ArrayList<>();
        }
    }

    public static void setCommands(List<String> commands,String name) {
        try {
            Entity entity = getEntityFromName(name);
            file = getFile(entity);
            data = YamlConfiguration.loadConfiguration(file);
            data.set("clickablemob.command",commands);
            save(file,data);
        } catch (NullPointerException exception) {
            // empty
        }
    }

    public static void executeCommands(CommandSender sender, Entity entity) {
        for (String command : getCommands(entity)) {
            getServer().dispatchCommand(sender,command);
        }
    }

    public static File getFile(Entity entity) {
        UUID id = entity.getUniqueId();
        File entityFile = new File(dataFolder,"clickablemobs/" + id + ".yml");
        if (entityFile.exists()) {
            return entityFile;
        }
        return null;
    }

    public static Entity getEntity(File file) {
        try {
            UUID id = UUID.fromString(getFileName(file));
            return Bukkit.getEntity(id);
        } catch (NullPointerException exception) {
            return null;
        }
    }

    public static Entity getEntityFromName(String name) {
        for (Map.Entry<UUID,String> set : getEntityMap().entrySet()) {
            if (Objects.equals(set.getValue(),name)) {
                return Bukkit.getEntity(set.getKey());
            }
        }
        return null;
    }

    public static String getFileName(File file) {
        return file.getName().substring(0,file.getName().length() - 4);
    }

    public static String getName(Entity entity) {
        try {
            file = getFile(entity);
            data = YamlConfiguration.loadConfiguration(file);
            return data.getString("clickablemob.name");
        } catch (NullPointerException exception) {
            return "";
        }
    }

    public static List<String> getEntityList() {
        List<String> list = new ArrayList<>();
        File[] fileList = new File(dataFolder,"clickablemobs/").listFiles();
        if (fileList != null) {
            for (File entityFile : fileList) {
                list.add(getFileName(entityFile));
            }
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    public static List<String> getEntityNames() {
        List<String> list = new ArrayList<>();
        for (String fileName : getEntityList()) {
            try {
                UUID id = UUID.fromString(fileName);
                Entity entity = Bukkit.getEntity(id);
                if (entity != null) {
                    list.add(getName(entity));
                }
            } catch (NullPointerException exception) {
                // empty
            }
        }
        return list;
    }

    public static HashMap<UUID,String> getEntityMap() {
        HashMap<UUID,String> map = new HashMap<>();
        for (String entityFile : getEntityList()) {
            try {
                UUID id = UUID.fromString(entityFile);
                Entity entity = Bukkit.getEntity(id);
                String name = getName(entity);
                map.put(id,name);
            } catch (NullPointerException exception) {
                // empty
            }
        }
        return map;
    }

    public static void deleteFile(Entity entity) {
        File file = getFile(entity);
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    public static boolean isResponsive(Entity entity) {
        try {
            file = getFile(entity);
            data = YamlConfiguration.loadConfiguration(file);
            return data.getBoolean("clickablemob.responsive");
        } catch (NullPointerException exception) {
            return false;
        }
    }

    public static void setResponsive(Entity entity, Boolean responsive) {
        try {
            file = getFile(entity);
            data = YamlConfiguration.loadConfiguration(file);
            data.set("clickablemob.responsive",responsive);
            save(file,data);
        } catch (NullPointerException exception) {
            // empty
        }
    }
}
