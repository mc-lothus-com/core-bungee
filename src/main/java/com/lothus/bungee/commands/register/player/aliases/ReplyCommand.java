package com.lothus.bungee.commands.register.player.aliases;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;
import java.util.Set;

public class ReplyCommand extends BungeeCommandBase {

    public ReplyCommand() {
        super("r", "reply");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cApenas jogadores podem utilizar este comando!");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        if (args.length >= 1) {
            if (!TellCommand.getConversation().containsKey(player.getUniqueId())) {
                player.sendMessage("§cVocê não está conversando com ninguém!");
                return;
            }

            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(TellCommand.getConversation().get(player.getUniqueId()));
            if (target == null) {
                player.sendMessage("§cO jogador com quem você está conversando não está online!");
                TellCommand.getConversation().remove(player.getUniqueId());
                return;
            }

            StringBuilder message = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) message.append(" ");
                message.append(args[i]);
            }
            ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "tell " + target.getName() + " " + message.toString());
            return;
        }
        player.sendMessage("§cUtilize /r <mensagem> para responder a um jogador!");
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
