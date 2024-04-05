package com.lothus.bungee.listeners.chat;

import com.lothus.core.Core;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerChatListener implements Listener {


    @EventHandler
    public void onCommand(ChatEvent event) {
        ProxiedPlayer sender = (ProxiedPlayer)event.getSender();
        String message = event.getMessage();
        ServerInfo serverInfo = Core.getServerController().get(sender.getServer().getInfo().getName());
        if (serverInfo == null)return;

        if (serverInfo.getType() != ServerType.LOGIN)return;

        if (message.startsWith("/register") || message.startsWith("/login") || message.startsWith("/logar") || message.startsWith("/registrar"))return;

        event.setCancelled(true);
        sender.sendMessage("§cVocê precisa de autenticar para usar o chat normalmente.");
    }

}
