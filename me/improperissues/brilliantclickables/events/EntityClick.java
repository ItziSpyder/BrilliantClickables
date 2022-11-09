package me.improperissues.brilliantclickables.events;

import me.improperissues.brilliantclickables.BrilliantClickables;
import me.improperissues.brilliantclickables.file.ClickableMobs;
import me.improperissues.brilliantclickables.other.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityClick implements Listener {

    static BrilliantClickables plugin = Messages.plugin;
    static HashMap<String,Long> clickCooldown = new HashMap<>();

    @EventHandler
    public static void PlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();

        if (isClickableMob(entity)) {
            if (clickCooldown.containsKey(p.getName()) && clickCooldown.get(p.getName()) > System.currentTimeMillis()) {
                return;
            }
            e.setCancelled(true);
            clickCooldown.put(p.getName(),System.currentTimeMillis() + (50));
            if (p.isSneaking() && p.isOp()) {
                openDeleteMenu(p,entity);
                return;
            }
            ClickableMobs.executeCommands(p,entity);
        }
    }

    @EventHandler
    public static void EntityDamageEvent(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (isClickableMob(entity)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public static void EntityCombustEvent(EntityCombustEvent e) {
        Entity entity = e.getEntity();
        if (isClickableMob(entity)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public static void InventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        String title = e.getView().getTitle();

        try {
            ItemStack item = e.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            String display = meta.getDisplayName();

            if (title.contains(Messages.starter) && !inv.getType().equals(InventoryType.PLAYER)) {
                e.setCancelled(true);
                if (!display.equals(" ")) {
                    p.playSound(p.getLocation(),Sound.UI_BUTTON_CLICK,10,10);
                }
                if (title.contains("cDelete §7")) {
                    String name = title.substring((Messages.starter + "cDelete §7\"").length(),title.length() - ("\" §c?").length());
                    switch (display) {
                        case "§cYes, delete this clickable mob!":
                            p.chat("/clickablemob delete " + name);
                            p.closeInventory();
                            break;
                        case "§aNo, I've changed my mind.":
                            p.closeInventory();
                            break;
                    }
                }
            }
        } catch (NullPointerException exception) {
            // empty
        }

    }

    public static boolean isClickableMob(Entity entity) {
        return entity instanceof LivingEntity && ClickableMobs.getFile(entity) != null;
    }

    public static List<String> getEntityTypes() {
        List<String> list = new ArrayList<>();
        for (EntityType type : EntityType.class.getEnumConstants()) {
            list.add(type.name().toLowerCase().trim());
        }
        list.remove("player");
        return list;
    }

    public static void openDeleteMenu(Player player, Entity entity) {
        if (!(isClickableMob(entity))) {
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING,1,10);
        String name = ClickableMobs.getName(entity);
        Inventory menu = Bukkit.createInventory(player,27, Messages.starter + "cDelete §7\"" + name + "\" §c?");

        ItemStack yes = new ItemStack(Material.RED_WOOL);
        ItemMeta yesM = yes.getItemMeta();
        yesM.setDisplayName("§cYes, delete this clickable mob!");
        yes.setItemMeta(yesM);
        ItemStack no = new ItemStack(Material.LIME_WOOL);
        ItemMeta noM = no.getItemMeta();
        noM.setDisplayName("§aNo, I've changed my mind.");
        no.setItemMeta(noM);
        ItemStack x = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta xM = no.getItemMeta();
        xM.setDisplayName(" ");
        x.setItemMeta(xM);

        ItemStack[] contents = {
                x,x,x,x,x,x,x,x,x,
                x,x,yes,x,x,x,no,x,x,
                x,x,x,x,x,x,x,x,x,
        };
        menu.setContents(contents);
        player.openInventory(menu);
    }

    public static void loopLookAtNearestPlayer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    for (Entity entity : p.getNearbyEntities(6,6,6)) {
                        if (isClickableMob(entity) && ClickableMobs.isResponsive(entity)) {
                            Location loc = entity.getLocation();
                            loc.setYaw((float) Math.random() * 360);
                            loc.setPitch((float) Math.random() * -5);
                            entity.teleport(loc);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin,0,60);
    }
}
