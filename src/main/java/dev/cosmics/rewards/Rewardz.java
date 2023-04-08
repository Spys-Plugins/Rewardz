package dev.cosmics.rewards;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Rewardz extends JavaPlugin {
    private YamlDocument config;
    private YamlDocument data;
    private RewardUserManager manager;
    private final NamespacedKey rewardKey = new NamespacedKey(this, "reward");

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            this.config = YamlDocument.create(new File(getDataFolder(), "config.yml"), Objects.requireNonNull(getResource("config.yml")),
                    //For some reason getkeys returns defaults so we have to disable them
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.DEFAULT);
        } catch (IOException e) {
            getLogger().severe("Failed to load config file, does the server have access to it");
        }
        try {
            data = YamlDocument.create(new File(getDataFolder(), "data.yml"), Objects.requireNonNull(getResource("data.yml")));
        } catch (IOException e) {
            getLogger().severe("Failed to load data file, does the server have access to it");
        }
        RewardUser.setConfig(config);
        manager = new RewardUserManager(config);
        manager.loadFromConfig(data);
        new EventManager(manager, this, rewardKey);
        new RewardCmd(this, rewardKey);
        new EventCmd(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        manager.saveToConfig(data);
    }

    @NotNull
    public YamlDocument getCfg() {
        return config;
    }

    public RewardUserManager getManager() {
        return manager;
    }

    public YamlDocument getData() {
        return data;
    }
}
