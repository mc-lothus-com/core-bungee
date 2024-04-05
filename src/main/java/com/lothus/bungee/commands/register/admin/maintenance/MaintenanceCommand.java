package com.lothus.bungee.commands.register.admin.maintenance;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.servers.status.ServerStatus;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MaintenanceCommand extends BungeeCommandBase {

    public MaintenanceCommand() {
        super("maintenance", "manutencao");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());

            if (lothPlayer == null) {
                return;
            }

            if (lothPlayer.getGroup().getRank() != Rank.CEO) {
                if (!lothPlayer.getGroup().containsPermission("command.maintenance")) {
                    sender.sendMessage("§cVocê não tem permissão para executar este comando!");
                    return;
                }
            }
        }

        if (args.length == 0) {
            sender.sendMessage("§cSintaxe incorreta, utilize '/maintenance [on/off] [servidor]'.");
            return;
        }

        if (args.length > 0) {
            String value = args[0];

            if (value.equalsIgnoreCase("on")) {
                Core.getServerInfo().setStatus(ServerStatus.MAINTENANCE_MODE);
                Core.getDataServer().update(Core.getServerInfo());

                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

                    if (lothPlayer.getGroup().getRank() != Rank.CEO) {
                        if (!lothPlayer.getGroup().containsPermission("maintenance.bypass")) {
                            player.disconnect("§cNossos servidores estão em manutenção no momento.\n§cTente novamente mais tarde.\n\n§cMais informações em: §emc-lothus.com/discord\n");
                        }
                    }
                }


                sender.sendMessage("§aO servidor " + Core.getServerInfo().getName() + " foi colocado em manutenção.");
                return;
            }

            if (value.equalsIgnoreCase("off")) {
                Core.getServerInfo().setStatus(ServerStatus.ONLINE);
                Core.getDataServer().update(Core.getServerInfo());
                sender.sendMessage("§aO servidor " + Core.getServerInfo().getName() + " foi removido da manutenção.");
                return;
            }
        }
    }
}
