package com.lothus.bungee.commands.register.admin.alert;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BroadcastCommand extends BungeeCommandBase {

    public BroadcastCommand() {
        super("bc", "broadcast");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
            if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.GER.ordinal())) {
                sender.sendMessage(NO_PERMISSION);
                return;
            }
        }
        if (args.length >= 1) {
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) message.append(" ");
                message.append(args[i]);
            }
            ProxyServer.getInstance().broadcast("");
            ProxyServer.getInstance().broadcast("§2§LLOTHUS §8- §f" + message.toString().replace("&", "§"));
            ProxyServer.getInstance().broadcast("");
            return;
        }
        sender.sendMessage("§cSintaxe incorreta, utilize '/broadcast [mensagen]'.");
    }

}
