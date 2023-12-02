package cn.sandtripper.minecraft.bcustomplayerevent;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandHandler extends Command {

    private BCustomPlayerEvent plugin;

    public CommandHandler(BCustomPlayerEvent plugin) {
        super("bcpe");
        this.plugin = plugin;
    }

    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            if (strings.length == 0) {
                commandSender.sendMessage("§c未知参数！");
            } else {
                String subCommand = strings[0].toLowerCase();
                switch (subCommand) {
                    case "reload":
                        plugin.reload();
                        commandSender.sendMessage("§b插件重载成功！");
                        break;
                    case "start":
                        plugin.start();
                        commandSender.sendMessage("§b插件已启动！");
                        break;
                    case "stop":
                        plugin.stop();
                        commandSender.sendMessage("§b插件已停止！");
                        break;
                    default:
                        commandSender.sendMessage("§c未知参数！");
                        break;
                }
            }
        } else {
            commandSender.sendMessage("§c你没有权限！");
        }
    }
}
