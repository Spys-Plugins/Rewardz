package dev.cosmics.rewards;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class EventCmd implements CommandExecutor {
    private final RewardUserManager manager;
    private final Rewardz plugin;
    public EventCmd(Rewardz plugin) {
        plugin.getCommand("event").setExecutor(this);
        manager = plugin.getManager();
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /event <player> to give them an event stat");
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            //While it is possible for a player to have never played before and need this stat, since it is given out for events, it is VERY unlikely
            if (player.hasPlayedBefore() || player.isOnline()) {
                sender.sendMessage("Giving " + player.getName() + " an event stat");
                manager.addEvent(player.getUniqueId());
            } else {
                sender.sendMessage("Player not found! Are you sure they have played before?");
            }
        });
        return true;
    }
}
