package cn.sandtripper.minecraft.bcustomplayerevent;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class EventsHandler implements Listener {

    public EventsHandler(ConfigManager configManager, ConfigManager playersConfigManager) {
        this.configManager = configManager;
        this.configVersion = -1;
        this.ignoreSet = new HashSet<String>();

        this.playersConfigManager = playersConfigManager;
        this.playersConfig = playersConfigManager.getConfig();
        this.playerSet = new HashSet<UUID>();
        for (String strId : playersConfig.getStringList("players")) {
            playerSet.add(UUID.fromString(strId));
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        updateConfig();
        if (ignoreSet.contains(event.getPlayer().getName())) {
            return;
        }
        if (isNewPlayer(event.getPlayer().getUniqueId())) {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                player.sendMessage(colorFormat(config.getString("player-first-join-message").replace("{PLAYER}", event.getPlayer().getName())));
            }
            //效率低，待优化
            List<String> oldList = playersConfig.getStringList("players");
            oldList.add(event.getPlayer().getUniqueId().toString());
            this.playersConfigManager.getConfig().set("players", oldList);
            this.playersConfigManager.saveConfig();
            this.playerSet.add(event.getPlayer().getUniqueId());
        } else {
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                player.sendMessage(colorFormat(config.getString("player-join-message").replace("{PLAYER}", event.getPlayer().getName())));
            }
        }

    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        updateConfig();
        if (ignoreSet.contains(event.getPlayer().getName())) {
            return;
        }
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(colorFormat(config.getString("player-leave-message").replace("{PLAYER}", event.getPlayer().getName())));
        }
    }

    boolean isNewPlayer(UUID uuid) {
        return !playerSet.contains(uuid);
    }

    private String colorFormat(String message) {
        if (message == null) {
            return "";
        }
        return message.replace("&", "§");
    }

    private void updateConfig() {
        if (configManager.isOutVersion(configVersion)) {
            configVersion = configManager.getVersion();
            config = configManager.getConfig();
            ignoreSet = new HashSet<>(config.getStringList("ignore-players"));
        }
    }


    private ConfigManager configManager;
    private Configuration config;
    private int configVersion;

    private ConfigManager playersConfigManager;
    private Configuration playersConfig;
    private HashSet<String> ignoreSet;

    private HashSet<UUID> playerSet;
}
