package com.lothus.bungee.listeners.ping;

import com.lothus.bungee.BungeeCore;
import com.lothus.core.Core;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingListener implements Listener {

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        event.getResponse().setDescription(BungeeCore.getInstance().getMotd().replace("&", "§"));
        event.getResponse().getPlayers().setMax(Core.getServerInfo().getConfiguration().getMaxPlayers());
    }

}
