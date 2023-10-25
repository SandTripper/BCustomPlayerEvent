package cn.sandtripper.minecraft.bcustomplayerevent;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigManager {
    private Configuration config;
    private final String configName;
    private final Plugin plugin;
    private final File configFile;
    private int version;

    public ConfigManager(Plugin plugin, String filename) {
        this.plugin = plugin;
        this.configName = filename;
        this.configFile = new File(plugin.getDataFolder(), filename);
        this.version = 1;
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            try {
                // Make sure that the parent directory exists.
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdirs();
                }
                // Copy the file from the resources.
                InputStream in = plugin.getResourceAsStream(configName);
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                } else {
                    throw new IOException("Resource '" + configName + "' is missing");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("无法保存默认配置文件: " + configName);
                e.printStackTrace();
            }
        }
    }

    public Configuration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public int getVersion() {
        return version;
    }

    public boolean isOutVersion(int version) {
        return version != this.version;
    }

    public void reloadConfig() {
        this.version++;
        try {
            saveDefaultConfig(); // Ensure the configuration file exists
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法加载配置文件!");
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        if (config != null && configFile.exists()) {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
            } catch (IOException e) {
                plugin.getLogger().severe("配置保存失败!请重试!");
                e.printStackTrace();
            }
        }
    }
}
