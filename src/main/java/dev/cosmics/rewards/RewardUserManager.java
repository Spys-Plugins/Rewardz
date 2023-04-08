package dev.cosmics.rewards;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.javatuples.Ennead;
import org.javatuples.Octet;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class RewardUserManager {
    private final YamlDocument config;
    private final ArrayList<RewardUser> userData = new ArrayList<>();
    public RewardUserManager(YamlDocument config) {
        this.config = config;
    }

    public void loadFromConfig(YamlDocument dataDoc) {
        var rewardSection = dataDoc.getSection("Rewards");
        if (rewardSection == null) {
            dataDoc.set("Rewards", new ArrayList<>());
            try {
                dataDoc.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        for (Object key : rewardSection.getKeys()) {
            final var uuid = UUID.fromString(key.toString());
            final var dataSection = dataDoc.getSection("Rewards." + key);
            final var killedPlayers = dataSection.getInt("killed");
            final var fishedFish = dataSection.getInt("fished");
            final var hostilesKilled = dataSection.getInt("hostiles");
            final var event = dataSection.getInt("event");
            final var boss = dataSection.getInt("boss");
            final var built = dataSection.getInt("built");
            final var mined = dataSection.getInt("mined");
            final var rewards = dataSection.getStringList("rewards");
            userData.add(new RewardUser(uuid, killedPlayers, fishedFish, hostilesKilled, event, boss, built, mined, rewards));
        }
    }

    public void saveToConfig(YamlDocument dataDoc) {
        for (RewardUser user : userData) {
            final var uuid = user.getUuid();
            final var killedPlayers = user.getKilled();
            final var fishedFish = user.getFished();
            final var hostilesKilled = user.getHostiles();
            final var event = user.getEvent();
            final var boss = user.getBoss();
            final var built = user.getBuilt();
            final var mined = user.getMined();
            var dataSection = dataDoc.getSection(uuid.toString());
            if (dataSection == null) dataSection = dataDoc.createSection("Rewards." + uuid);
            dataSection.set("killed", killedPlayers);
            dataSection.set("fished", fishedFish);
            dataSection.set("hostiles", hostilesKilled);
            dataSection.set("event", event);
            dataSection.set("boss", boss);
            dataSection.set("built", built);
            dataSection.set("mined", mined);
            dataSection.set("rewards", user.getRewards());
        }
        try {
            dataDoc.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean has(UUID uuid) {
        for (RewardUser user : userData) {
            if (user.getUuid().equals(uuid)) return true;
        }
        return false;
    }

    public RewardUser get(UUID uuid) {
        for (RewardUser user : userData) {
            if (user.getUuid().equals(uuid)) return user;
        }
        return null;
    }

    public void addEmpty(UUID uniqueId) {
        userData.add(new RewardUser(uniqueId));
    }
}