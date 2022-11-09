package me.improperissues.brilliantclickables.events;

import me.improperissues.brilliantclickables.file.ExecuteBlocks;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class BlockClick implements Listener {

    static HashMap<String,Long> clickCooldown = new HashMap<>();

    @EventHandler
    public static void PlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        try {
            Block block = e.getClickedBlock();
            if (isClickableBlock(block)) {
                e.setCancelled(true);
                switch (e.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        if (clickCooldown.containsKey(p.getName()) && clickCooldown.get(p.getName()) > System.currentTimeMillis()) {
                            return;
                        }
                        clickCooldown.put(p.getName(),System.currentTimeMillis() + (50));
                        ExecuteBlocks.executeAll(p,block);
                        break;
                }
            }
        } catch (NullPointerException exception) {
            // empty
        }
    }

    @EventHandler
    public static void EntityExplodeEvent(EntityExplodeEvent e) {
        List<Block> blocks = e.blockList();
        Entity entity = e.getEntity();
        int affected = 0;
        for (Block block : blocks) {
            if (isClickableBlock(block)) {
                e.setCancelled(true);
                affected ++;
            }
        }
        if (affected > 0) {
            getServer().getLogger().warning("An explosion was cancelled @" + entity.getLocation() + " because " + affected + " clickable blocks were affected!");
        }
    }

    @EventHandler
    public static void BlockExplodeEvent(BlockExplodeEvent e) {
        List<Block> blocks = e.blockList();
        Block b = e.getBlock();
        int affected = 0;
        for (Block block : blocks) {
            if (isClickableBlock(block)) {
                e.setCancelled(true);
                affected ++;
            }
        }
        if (affected > 0) {
            getServer().getLogger().warning("An explosion was cancelled @" + b.getLocation() + " because " + affected + " clickable blocks were affected!");
        }
    }

    public static boolean isClickableBlock(Block block) {
        File file = ExecuteBlocks.getFile(block);
        return file.exists();
    }
}
