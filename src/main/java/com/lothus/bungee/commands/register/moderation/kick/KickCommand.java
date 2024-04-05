package com.lothus.bungee.commands.register.moderation.kick;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class KickCommand extends BungeeCommandBase {

    public KickCommand() {
        super("kick", "expulsar","desconectar");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
            if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal())) {
                if (!lothPlayer.getGroup().containsPermission("command.kick")) {
                    sender.sendMessage(NO_PERMISSION);
                    return;
                }
            }
        }

        if (args.length > 0) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target ==null){
                sender.sendMessage("§cJogador não encontrado.");
                return;
            }

            target.disconnect(
                    "§c§lLOTHUS\n\n§cVocê foi desconectado por " + ((sender instanceof ProxiedPlayer) ? ((ProxiedPlayer) sender).getDisplayName() : "CONSOLE") + "§c.\n\n"
            );
            sender.sendMessage("§eVocê expulsou §b" + target.getName() + "§e.");
        }
    }
}
