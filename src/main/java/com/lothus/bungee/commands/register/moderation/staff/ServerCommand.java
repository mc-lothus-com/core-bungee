package com.lothus.bungee.commands.register.moderation.staff;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ServerCommand extends BungeeCommandBase {

    public ServerCommand() {
        super("server");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cApenas jogadores podem utilizar este comando!");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        LothPlayer profile = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
        if (!(profile.getGroup().getRank().ordinal() <= Rank.GER.ordinal())) {
            sender.sendMessage(NO_PERMISSION);
            return;
        }
        if (args.length == 1) {
            if (ProxyServer.getInstance().getServerInfo(args[0]) == null) {
                sender.sendMessage("§cServidor não encontrado!");
                return;
            }
            sender.sendMessage("§7Enviando você para " + ProxyServer.getInstance().getServerInfo(args[0]).getName() + "...");
            player.connect(ProxyServer.getInstance().getServerInfo(args[0]));
            return;
        }
        sender.sendMessage("§cSintaxe incorreta, utilize '/server [servidor]'.");
    }

}
