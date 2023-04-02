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
    private final ArrayList<Ennead<UUID, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<String>>> userData = new ArrayList<>();
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
            userData.add(new Ennead<>(uuid, killedPlayers, fishedFish, hostilesKilled, event, boss, built, mined, rewards ));
        }
    }

    public void saveToConfig(YamlDocument dataDoc) {
        for (Ennead<UUID, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<String>> user : userData) {
            final var uuid = user.getValue0();
            final var killedPlayers = user.getValue1();
            final var fishedFish = user.getValue2();
            final var hostilesKilled = user.getValue3();
            final var event = user.getValue4();
            final var boss = user.getValue5();
            final var built = user.getValue6();
            final var mined = user.getValue7();
            var dataSection = dataDoc.getSection(uuid.toString());
            if (dataSection == null) dataSection = dataDoc.createSection("Rewards." + uuid);
            dataSection.set("killed", killedPlayers);
            dataSection.set("fished", fishedFish);
            dataSection.set("hostiles", hostilesKilled);
            dataSection.set("event", event);
            dataSection.set("boss", boss);
            dataSection.set("built", built);
            dataSection.set("mined", mined);
            dataSection.set("rewards", user.getValue8());
        }
        try {
            dataDoc.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Ennead<UUID, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<String>> get(UUID uuid) {
        for (Ennead<UUID, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<String>> user : userData) {
            if (user.getValue0().equals(uuid)) return user;
        }
        return null;
    }

    public void setUserData(Ennead<UUID, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<String>> data) {
        for (Ennead<UUID, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<String>> user : userData) {
            if (user.getValue0().equals(data.getValue0())) {
                userData.remove(user);
                userData.add(data);
                return;
            }
        }
    }

    public boolean has(UUID uuid) {
        for (Ennead<UUID, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<String>> user : userData) {
            if (user.getValue0().equals(uuid)) return true;
        }
        return false;
    }

    public void addEmpty(UUID uuid) {
        userData.add(new Ennead<>(uuid, 0, 0, 0, 0, 0, 0, 0, new ArrayList<>()));
    }

    public void addKilledPlayer(UUID uniqueId) {
        setUserData(get(uniqueId).setAt1(get(uniqueId).getValue1() + 1));
        if (config.getIntList("Milestones.killed").contains(get(uniqueId).getValue1())) {
            giveReward(uniqueId);
        }
    }

    public void addKilledMob(UUID uniqueId) {
        setUserData(get(uniqueId).setAt3(get(uniqueId).getValue3() + 1));
        if (config.getIntList("Milestones.hostiles").contains(get(uniqueId).getValue3())) {
            giveReward(uniqueId);
        }
    }

    public void addFished(UUID uniqueId) {
        setUserData(get(uniqueId).setAt2(get(uniqueId).getValue2() + 1));
        if (config.getIntList("Milestones.fished").contains(get(uniqueId).getValue2())) {
            giveReward(uniqueId);
        }
    }

    public void addEvent(UUID uniqueId) {
        setUserData(get(uniqueId).setAt4(get(uniqueId).getValue4() + 1));
        if (config.getIntList("Milestones.event").contains(get(uniqueId).getValue4())) {
            giveReward(uniqueId);
        }
    }

    public void addBoss(UUID uniqueId) {
        setUserData(get(uniqueId).setAt5(get(uniqueId).getValue5() + 1));
        giveReward(uniqueId);
    }

    public void addBuilt(UUID uniqueId) {
        setUserData(get(uniqueId).setAt6(get(uniqueId).getValue6() + 1));
        if (config.getIntList("Milestones.built").contains(get(uniqueId).getValue6())) {
            giveReward(uniqueId);
        }
    }

    public void addMined(UUID uniqueId) {
        setUserData(get(uniqueId).setAt7(get(uniqueId).getValue7() + 1));
        if (config.getIntList("Milestones.mined").contains(get(uniqueId).getValue7())) {
            giveReward(uniqueId);
        }
    }

    public void giveReward(UUID uuid) {
        int roll = config.getKeys().size() - 1;
        int i = ThreadLocalRandom.current().nextInt(0, roll);
        var reward = config.getSection("Rewards." + config.getSection("Rewards").getKeys().toArray()[i].toString());
        Objects.requireNonNull(get(uuid)).getValue8().add(reward.getNameAsString());
        Bukkit.getPlayer(uuid).sendMessage(Component.text("You got a reward! To claim it, run /rewards", NamedTextColor.GREEN));
    }
}