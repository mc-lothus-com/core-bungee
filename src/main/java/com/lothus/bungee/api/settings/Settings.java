package com.lothus.bungee.api.settings;

import com.lothus.bungee.BungeeCore;
import com.lothus.bungee.api.yaml.YamlConfig;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Settings {

    protected static YamlConfig config;
    public static Configuration configuration;

    public Settings() {
        config = null;

        try {
            (config = new YamlConfig("config.yml", BungeeCore.getInstance())).saveDefaultConfig();
            config.loadConfig();
            configuration = config.getConfig();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void createConfig(File file, Configuration config) {
        try {
            if (!BungeeCore.getInstance().getDataFolder().exists()) {
                BungeeCore.getInstance().getDataFolder().mkdirs();
            }
            file = new File(BungeeCore.getInstance().getDataFolder(), "config.yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(BungeeCore.getInstance().getDataFolder(), "config.yml"));
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveConfig() {
        try {
            config.saveConfig();
        } catch (IOException var1) {
            var1.printStackTrace();
        }
    }
}