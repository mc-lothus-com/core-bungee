package com.lothus.bungee.commands.register.player.party;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.player.party.Party;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class PCommand extends BungeeCommandBase {

    public PCommand() {
        super(
                "p",
                "pc"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer))return;

        ProxiedPlayer player = (ProxiedPlayer) sender;

        Party party = Core.getPartyController().get(player.getUniqueId());

        if (party == null) {
            player.sendMessage("§cVocê não está em uma party.");
            return;
        }

        if (!party.isChat() && !party.isLeader(player.getUniqueId())) {
            player.sendMessage("§cO chat da party está desativado, portanto você não pode enviar mensagens.");
            return;
        }

        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        if (args.length > 0) {
            for (UUID uniqueId : party.getMembers()) {
                ProxiedPlayer find = ProxyServer.getInstance().getPlayer(uniqueId);

                if (find == null) {
                    party.remove(uniqueId);
                    party.removeRequest(uniqueId);
                    continue;
                }

                find.sendMessage("§d[PARTY] " + (lothPlayer.getGroup().getTag() == Rank.MEMBRO ? "§7" : lothPlayer.getGroup().getTag().getColor() + "§l" + lothPlayer.getGroup().getTag().getName().toUpperCase() + " " + lothPlayer.getGroup().getTag().getColor()) + player.getName() + ": §7" + getArgs(args, 0));
            }
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(party.getLeader());
            target.sendMessage("§d[PARTY] " + (lothPlayer.getGroup().getTag() == Rank.MEMBRO ? "§7" : lothPlayer.getGroup().getTag().getColor() + "§l" + lothPlayer.getGroup().getTag().getName().toUpperCase() + " " + lothPlayer.getGroup().getTag().getColor()) + player.getName() + ": §7" + getArgs(args, 0));
        }
    }
}
