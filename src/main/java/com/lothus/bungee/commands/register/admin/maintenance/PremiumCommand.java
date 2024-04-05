package com.lothus.bungee.commands.register.admin.maintenance;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.servers.type.ProxyType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PremiumCommand extends BungeeCommandBase {

    public PremiumCommand() {
        super("premium");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());

            if (lothPlayer == null) {
                return;
            }

            if (lothPlayer.getGroup().getRank() != Rank.CEO) {
                if (!lothPlayer.getGroup().containsPermission("command.premium")) {
                    sender.sendMessage("§cVocê não tem permissão para executar este comando!");
                    return;
                }
            }
        }

        Core.getServerInfo().setProxyType((Core.getServerInfo().getProxyType() == ProxyType.PREMIUM) ? ProxyType.CRACKED : ProxyType.PREMIUM);
        sender.sendMessage("§cO servidor agora está em modo " + Core.getServerInfo().getProxyType().name() + "!");
        Core.getDataServer().update(Core.getServerInfo());
    }
}
