package cn.sandtripper.minecraft.bcustomplayerevent;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandHandler extends Command {
    public CommandHandler(ConfigManager configManager) {
        super("bcpe");
        this.configManager = configManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            reload(commandSender);
        } else {
            commandSender.sendMessage("§c你没有权限！");
        }
    }

    private void reload(CommandSender commandSender) {
        configManager.reloadConfig();
        commandSender.sendMessage("§b插件重载成功！");
    }

    private ConfigManager configManager;
}
