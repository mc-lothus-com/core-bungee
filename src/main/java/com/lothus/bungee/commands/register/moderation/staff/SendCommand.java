package com.lothus.bungee.commands.register.moderation.staff;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;

public class SendCommand extends BungeeCommandBase {

    public SendCommand() {
        super("send", "mover", "puxar");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer profile = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
            if (!(profile.getGroup().getRank().ordinal() <= Rank.GER.ordinal())) {
                sender.sendMessage(NO_PERMISSION);
                return;
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("all")) {
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(args[1]);
                if (serverInfo == null) {
                    sender.sendMessage("§cServidor não encontrado!");
                    return;
                }
                for (ProxiedPlayer o : ProxyServer.getInstance().getPlayers()) {
                    o.connect(serverInfo);
                }
                sender.sendMessage("§aVocê moveu todos os jogadores para " + serverInfo.getName() + ".");
                return;
            }
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cUsuário não encontrado!");
                return;
            }
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(args[1]);
            if (serverInfo == null) {
                sender.sendMessage("§cServidor não encontrado!");
                return;
            }
            target.connect(serverInfo);
            sender.sendMessage("§aVocê moveu " + target.getName() + " §apara " + serverInfo.getName() + ".");
            target.sendMessage("§aVocê foi movido(a) para o servidor " + serverInfo.getName() + ".");
            return;
        }
        sender.sendMessage("§cSintaxe incorreta! Utilize: /send <usuário | all> <server>");
    }

    public Iterable<String> onTabComplete(CommandSender cs, String[] args) {
        Set<String> match = new HashSet<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.getName().toLowerCase().startsWith(search)) {
                    match.add(player.getName());
                }
            }
        }
        return match;
    }

}
