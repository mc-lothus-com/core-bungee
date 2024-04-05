package com.lothus.bungee.commands.register.admin.maintenance;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.servers.status.ServerStatus;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BetaCommand extends BungeeCommandBase {

    public BetaCommand() {
        super("beta");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());

            if (lothPlayer == null) {
                return;
            }

            if (lothPlayer.getGroup().getRank() != Rank.CEO) {
                if (!lothPlayer.getGroup().containsPermission("command.beta")) {
                    sender.sendMessage("§cVocê não tem permissão para executar este comando!");
                    return;
                }
            }
        }

        if (args.length == 0) {
            if (Core.getServerInfo().getStatus() != ServerStatus.BETA_MODE) {
                Core.getServerInfo().setStatus(ServerStatus.BETA_MODE);
                Core.getDataServer().update(Core.getServerInfo());
            } else {
                Core.getServerInfo().setStatus(ServerStatus.ONLINE);
                Core.getDataServer().update(Core.getServerInfo());
            }
            sender.sendMessage("§aModo beta alterada para " + (Core.getServerInfo().getStatus() == ServerStatus.BETA_MODE ? "BETA" : "NORMAL") + " com sucesso!");
            return;
        }

    }
}
