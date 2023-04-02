package dev.cosmics.rewards;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class RewardCmd implements CommandExecutor {
    private final YamlDocument config;
    private final RewardUserManager manager;
    private final NamespacedKey rewardKey;
    public RewardCmd(Rewardz plugin, NamespacedKey rewardKey) {
        plugin.getCommand("rewards").setExecutor(this);
        config = plugin.getCfg();
        manager = plugin.getManager();
        this.rewardKey = rewardKey;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("rewards.reload")) {
            try {
                config.reload();
            } catch (IOException e) {
                sender.sendMessage(Component.text("Failed to reload config", NamedTextColor.RED));
                return true;
            }
            sender.sendMessage(Component.text("Reloaded config", NamedTextColor.GREEN));
            return true;
        }
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Component.text("You must be a player to use this command", NamedTextColor.RED));
            return true;
        }
        new RewardGui(p, manager, config, rewardKey).open();
        return true;
    }
}
