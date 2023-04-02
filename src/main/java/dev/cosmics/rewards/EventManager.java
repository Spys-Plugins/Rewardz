package dev.cosmics.rewards;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Level;

public final class EventManager implements Listener {
    private final RewardUserManager manager;
    private final Rewardz plugin;
    private final NamespacedKey rewardKey;
    public EventManager(RewardUserManager manager, Rewardz plugin, NamespacedKey rewardKey) {
        this.manager = manager;
        this.plugin = plugin;
        this.rewardKey = rewardKey;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        if (manager.has(event.getUniqueId())) return;
        manager.addEmpty(event.getUniqueId());
        plugin.getLogger().log(Level.FINE, "Added new user to data file");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKillPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player p)) return;
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (damaged.getHealth() - event.getFinalDamage() > 0) return;
        manager.addKilledPlayer(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKillMob(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player p)) return;
        if (!(event.getEntity() instanceof Monster m)) return;
        if (m.getHealth() - event.getFinalDamage() > 0) return;
        manager.addKilledMob(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        manager.addFished(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void bossDeath(MythicMobDeathEvent event) {
        if (!(event.getKiller() instanceof Player p)) return;
        manager.addBoss(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMined(BlockBreakEvent event) {
        manager.addMined(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        manager.addBuilt(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player p)) return;
        if (event.getClickedInventory() == null) return;
        if (event.getView().getTitle().contains("Rewards")) event.setCancelled(true);
        else return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().displayName() == null) return;
        if (event.getCurrentItem().getItemMeta().lore() == null) return;
        ItemStack clicked = event.getCurrentItem();
        String reward = clicked.getItemMeta().getPersistentDataContainer().get(rewardKey, PersistentDataType.STRING);
        if (reward == null) return;
        if (plugin.getCfg().getSection("Rewards." + reward) == null) return;
        var playerData = manager.get(p.getUniqueId());
        if (playerData == null) return;
        if (!playerData.getValue8().contains(reward)) return;
        playerData.getValue8().remove(reward);
        System.out.println(PlaceholderAPI.setPlaceholders(p, reward));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
        p.closeInventory(InventoryCloseEvent.Reason.CANT_USE);
    }
}
