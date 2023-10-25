package cn.sandtripper.minecraft.bcustomplayerevent;

import net.md_5.bungee.api.plugin.Plugin;


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
        this.playersConfigManager.saveDefaultConfig();
        getProxy().getPluginManager().registerListener(this, new EventsHandler(configManager, playersConfigManager));//注册监听器
        getProxy().getPluginManager().registerCommand(this, new CommandHandler(configManager));
        //显示加载文字
        for (int i = 0; i < enableTexts.length; i++) {
            getLogger().info(enableTexts[i]);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private ConfigManager configManager;
    private ConfigManager playersConfigManager;
}
