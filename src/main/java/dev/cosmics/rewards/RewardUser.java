package dev.cosmics.rewards;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RewardUser {
    private static YamlDocument config;
    private final UUID uuid;
    private int killed;
    private int fished;
    private int hostiles;
    private int event;
    private int  boss;
    private int built;
    private int mined;
    private final ArrayList<String> rewards = new ArrayList<>();
    public RewardUser(UUID uuid) {
        this.uuid = uuid;
        this.killed = 0;
        this.fished = 0;
        this.hostiles = 0;
        this.event = 0;
        this.boss = 0;
        this.built = 0;
        this.mined = 0;
    }

    public RewardUser(UUID uuid, int killed, int fished, int hostiles, int event, int boss, int built, int mined, List<String> rewards) {
        this.uuid = uuid;
        this.killed = killed;
        this.fished = fished;
        this.hostiles = hostiles;
        this.event = event;
        this.boss = boss;
        this.built = built;
        this.mined = mined;
        this.rewards.addAll(rewards);
    }

    public static void setConfig(YamlDocument config) {
        RewardUser.config = config;
    }

    public void addReward(RewardType type) {
        final Section section = config.getSection("Rewards." + type.typeName);
        final int amount = section.getKeys().size();
        final int roll;
        if (amount == 0) {
            roll = 0;
        } else {
            roll = ThreadLocalRandom.current().nextInt(0, amount);
        }
        System.out.println("Roll: " + roll);
        rewards.add(type.typeName + "." + section.getSection(section.getKeys().toArray()[roll].toString()).getNameAsString());
    }
    public void checkReward(RewardType type) {
        if (Objects.requireNonNull(type) == RewardType.BOSS) {
            addReward(RewardType.BOSS);
        } else {
            try {
                var field = getClass().getDeclaredField(type.typeName);
                field.setAccessible(true);

                if (config.getIntList("Rewards." + type.typeName).contains(field.getInt(this))) {
                    addReward(type);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(RewardType type, int amount) {
        try {
            var field = getClass().getDeclaredField(type.typeName);
            field.setAccessible(true);
            field.set(this, field.getInt(this) + amount);
            field.setAccessible(false);
            checkReward(type);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getBoss() {
        return boss;
    }

    public int getBuilt() {
        return built;
    }

    public int getEvent() {
        return event;
    }

    public int getFished() {
        return fished;
    }

    public int getHostiles() {
        return hostiles;
    }

    public int getKilled() {
        return killed;
    }

    public int getMined() {
        return mined;
    }

    public ArrayList<String> getRewards() {
        return rewards;
    }
    
    public enum RewardType {
        KILLED_PLAYERS("killed"),
        FISHED_FISH("fished"),
        HOSTILES_KILLED("hostiles"),
        EVENT("event"),
        BOSS("boss"),
        BUILT("built"),
        MINED("mined");
        private final String typeName;

        RewardType(String typeName) {
            this.typeName = typeName;
        }
        public String getTypeName() {
            return typeName;
        }
    }
}
