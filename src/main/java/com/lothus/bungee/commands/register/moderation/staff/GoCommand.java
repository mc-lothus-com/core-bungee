package com.lothus.bungee.commands.register.moderation.staff;

import com.lothus.bungee.BungeeCore;
import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class GoCommand extends BungeeCommandBase {

    public GoCommand() {
        super("go");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cApenas jogadores podem utilizar este comando!");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.MIDIA.ordinal())) {
            sender.sendMessage(NO_PERMISSION);
            return;
        }
        if (args.length == 1) {
            if (ProxyServer.getInstance().getPlayer(args[0]) == null) {
                sender.sendMessage("§cUsuário não encontrado!");
                return;
            }
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[0]);
            if (p.getServer().getInfo().equals(player.getServer().getInfo())) {
                player.chat("/tp " + p.getName());
                return;
            }

            player.connect(p.getServer().getInfo());
            ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), new Runnable() {
                @Override
                public void run() {
                    player.chat("/tp " + p.getName());
                }
            }, 1, TimeUnit.SECONDS);
            return;
        }
        sender.sendMessage("§cSintaxe incorreta, utilize '/go [player]'.");
    }

}
