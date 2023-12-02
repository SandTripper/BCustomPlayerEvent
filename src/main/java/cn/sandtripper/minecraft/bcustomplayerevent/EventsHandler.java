package cn.sandtripper.minecraft.bcustomplayerevent;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventsHandler implements Listener {

    private BCustomPlayerEvent plugin;

    public EventsHandler(BCustomPlayerEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        plugin.playerLogin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        plugin.playerDisconnect(event.getPlayer());
    }


}
