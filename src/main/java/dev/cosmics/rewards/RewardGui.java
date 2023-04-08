package dev.cosmics.rewards;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.javatuples.Ennead;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RewardGui {
    private final Inventory inventory;
    private final Player player;
    private final NamespacedKey rewardKey;
    public RewardGui(Player player, RewardUserManager manager, YamlDocument config, NamespacedKey rewardKey) {
        this.player = player;
        this.rewardKey = rewardKey;
        var userData = manager.get(player.getUniqueId());
        assert userData != null;
        inventory = Bukkit.createInventory(null, Math.floorDiv(userData.getRewards().size(), 9) * 9 + 9, Component.text("Rewards"));
        addItems(config, userData);
    }
    private void addItems(YamlDocument config, RewardUser userData) {
        int i = 0;
        for (String reward : userData.getRewards()) {
            final var section = config.getSection("Rewards." + reward);
            if (section == null) continue;
            final var item = new ItemStack(Material.valueOf(section.getString("item").toUpperCase()));
            final var meta = item.getItemMeta();
            assert meta != null;
            meta.displayName(MiniMessage.miniMessage().deserialize(section.getString("Display Name")));
            final var lore = new ArrayList<Component>();
            for (String line : section.getStringList("Lore")) {
                lore.add(MiniMessage.miniMessage().deserialize(line));
            }
            meta.lore(lore);
            meta.getPersistentDataContainer().set(rewardKey, PersistentDataType.STRING, reward);
            item.setItemMeta(meta);
            inventory.setItem(i, item);
            i++;
        }
    }

    public void open() {
        player.openInventory(inventory);
    }
}
