package cn.sandtripper.minecraft.bcustomplayerevent;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.*;


public final class BCustomPlayerEvent extends Plugin {
    private static final String[] enableTexts = {
            "\033[36m  ____   _____          _                  _____  _                       ______               _   \033[0m",
            "\033[36m |  _ \\ / ____|        | |                |  __ \\| |                     |  ____|             | |  \033[0m",
            "\033[36m | |_) | |    _   _ ___| |_ ___  _ __ ___ | |__) | | __ _ _   _  ___ _ __| |____   _____ _ __ | |_ \033[0m",
            "\033[36m |  _ <| |   | | | / __| __/ _ \\| '_ ` _ \\|  ___/| |/ _` | | | |/ _ \\ '__|  __\\ \\ / / _ \\ '_ \\| __|\033[0m",
            "\033[36m | |_) | |___| |_| \\__ \\ || (_) | | | | | | |    | | (_| | |_| |  __/ |  | |___\\ V /  __/ | | | |_ \033[0m",
            "\033[36m |____/ \\_____\\__,_|___/\\__\\___/|_| |_| |_|_|    |_|\\__,_|\\__, |\\___|_|  |______\\_/ \\___|_| |_|\\__|\033[0m",
            "\033[36m                                                           __/ |                                   \033[0m",
            "\033[36m                                                          |___/                                   \033[0m",
            "\033[36m玩家事件自定义 - by SandTripper\033[0m",
            "\033[36m启动成功!\033[0m"
    };

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.configManager = new ConfigManager(this, "config.yml");
        this.playersConfigManager = new ConfigManager(this, "players.yml");
        this.configManager.saveDefaultConfig();
        getConfigTmpVar();
        this.playersConfigManager.saveDefaultConfig();
        this.playersConfig = this.playersConfigManager.getConfig();
        this.joinLeaveQue = new ArrayDeque<>();
        this.firstJoinQue = new ArrayDeque<>();
        this.isStopped = false;
        getProxy().getPluginManager().registerListener(this, new EventsHandler(this));//注册监听器
        getProxy().getPluginManager().registerCommand(this, new CommandHandler(this));
        //显示加载文字
        for (int i = 0; i < enableTexts.length; i++) {
            getLogger().info(enableTexts[i]);
        }

        this.playerSet = new HashSet<UUID>();
        for (String strId : playersConfig.getStringList("players")) {
            playerSet.add(UUID.fromString(strId));
        }
    }

    private void getConfigTmpVar() {
        config = configManager.getConfig();
        ignoreSet = new HashSet<>(config.getStringList("ignore-players"));
        playerJoinLeaveSeconds = config.getInt("player-join-leave-seconds");
        playerJoinLeaveLimit = config.getInt("player-join-leave-limit");
        playerFirstJoinSeconds = config.getInt("player-first-join-seconds");
        playerFirstJoinLimit = config.getInt("player-first-join-limit");
        notWriteFirstJoin = config.getBoolean("not-write-first-join");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reload() {
        configManager.reloadConfig();
        getConfigTmpVar();
    }

    public void playerLogin(ProxiedPlayer player) {
        if (isStopped) {
            return;
        }
        if (ignoreSet.contains(player.getName())) {
            return;
        }
        if (isNewPlayer(player.getUniqueId())) {
            boolean check = checkUpdatefirstJoinLimit();
            if (check) {
                for (ProxiedPlayer sendPlayer : ProxyServer.getInstance().getPlayers()) {
                    sendPlayer.sendMessage(colorFormat(config.getString("player-first-join-message").replace("{PLAYER}", player.getName())));
                }
            }
            if (check || !notWriteFirstJoin) {
                //效率低，待优化
                List<String> oldList = playersConfig.getStringList("players");
                oldList.add(player.getUniqueId().toString());
                this.playersConfigManager.getConfig().set("players", oldList);
                this.playersConfigManager.saveConfig();
                this.playerSet.add(player.getUniqueId());
            }
        } else {
            boolean check = checkUpdateJoinLeaveLimit();
            if (check) {
                for (ProxiedPlayer sendPlayer : ProxyServer.getInstance().getPlayers()) {
                    sendPlayer.sendMessage(colorFormat(config.getString("player-join-message").replace("{PLAYER}", player.getName())));
                }
            }
        }
    }

    public void playerDisconnect(ProxiedPlayer player) {
        if (isStopped) {
            return;
        }
        if (ignoreSet.contains(player.getName())) {
            return;
        }
        boolean check = checkUpdateJoinLeaveLimit();
        if (check) {
            for (ProxiedPlayer sendPlayer : ProxyServer.getInstance().getPlayers()) {
                player.sendMessage(colorFormat(config.getString("player-leave-message").replace("{PLAYER}", sendPlayer.getName())));
            }
        }
    }

    boolean checkUpdateJoinLeaveLimit() {
        long currentTime = System.currentTimeMillis() / 1000;
        joinLeaveQue.offer(currentTime);
        while (!joinLeaveQue.isEmpty()) {
            if (currentTime - joinLeaveQue.peek() > playerJoinLeaveSeconds) {
                joinLeaveQue.poll();
            } else {
                break;
            }
        }
        return joinLeaveQue.size() <= playerJoinLeaveLimit;
    }

    boolean checkUpdatefirstJoinLimit() {
        long currentTime = System.currentTimeMillis() / 1000;
        firstJoinQue.offer(currentTime);
        while (!firstJoinQue.isEmpty()) {
            if (currentTime - firstJoinQue.peek() > playerFirstJoinSeconds) {
                firstJoinQue.poll();
            } else {
                break;
            }
        }
        return firstJoinQue.size() <= playerFirstJoinLimit;
    }


    private String colorFormat(String message) {
        if (message == null) {
            return "";
        }
        return message.replace("&", "§");
    }

    public boolean isNewPlayer(UUID uuid) {
        return !playerSet.contains(uuid);
    }

    public void stop() {
        isStopped = true;
    }

    public void start() {
        isStopped = false;
    }


    private Configuration config;
    private Configuration playersConfig;
    private HashSet<String> ignoreSet;
    private HashSet<UUID> playerSet;
    private ConfigManager configManager;
    private ConfigManager playersConfigManager;

    private Queue<Long> joinLeaveQue;
    private Queue<Long> firstJoinQue;

    private int playerJoinLeaveSeconds;
    private int playerJoinLeaveLimit;
    private int playerFirstJoinSeconds;
    private int playerFirstJoinLimit;
    private boolean notWriteFirstJoin;

    boolean isStopped;
}
