package dev.cosmics.rewards;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.clip.placeholderapi.PlaceholderAPI;
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
        manager.get(p.getUniqueId()).add(RewardUser.RewardType.KILLED_PLAYERS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKillMob(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player p)) return;
        if (!(event.getEntity() instanceof Monster m)) return;
        if (m.getHealth() - event.getFinalDamage() > 0) return;
        manager.get(p.getUniqueId()).add(RewardUser.RewardType.HOSTILES_KILLED, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        manager.get(event.getPlayer().getUniqueId()).add(RewardUser.RewardType.FISHED_FISH, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void bossDeath(MythicMobDeathEvent event) {
        if (!(event.getKiller() instanceof Player p)) return;
        manager.get(p.getUniqueId()).add(RewardUser.RewardType.BOSS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMined(BlockBreakEvent event) {
        manager.get(event.getPlayer().getUniqueId()).add(RewardUser.RewardType.MINED, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        manager.get(event.getPlayer().getUniqueId()).add(RewardUser.RewardType.BUILT, 1);
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
        if (!playerData.getRewards().contains(reward)) return;
        playerData.getRewards().remove(reward);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward.split("\\.")[1]));
        p.closeInventory(InventoryCloseEvent.Reason.CANT_USE);
    }
}
