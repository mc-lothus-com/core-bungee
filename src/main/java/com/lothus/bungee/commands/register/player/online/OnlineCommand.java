package com.lothus.bungee.commands.register.player.online;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class OnlineCommand extends BungeeCommandBase {

    public OnlineCommand() {
        super("onlines", "online", "on");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("");
        sender.sendMessage("§eExistem §d" + Core.getServerInfo().getPlayers() + " §ejogadores online na rede.");
        if (sender instanceof ProxiedPlayer) {
            ServerInfo serverInfo = Core.getServerController().get(((ProxiedPlayer) sender).getServer().getInfo().getName());
            sender.sendMessage("§eExistem §d" + serverInfo.getPlayers() + " §eno seu servidor.");
        }
        sender.sendMessage("");
    }

    private int players(ServerType type) {
        List<ServerInfo> s = Core.getServerController().get(type);

        if (s.isEmpty())return 0;

        int players = 0;
        for (ServerInfo serverInfo : s) {
            players+= serverInfo.getPlayers();
        }
        return players;
    }
}
