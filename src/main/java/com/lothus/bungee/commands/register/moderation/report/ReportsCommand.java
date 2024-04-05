package com.lothus.bungee.commands.register.moderation.report;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.bungee.commands.register.moderation.report.menu.ReportMenu;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportsCommand extends BungeeCommandBase {

    public ReportsCommand() {
        super("reports", "reportes");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
            if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal())) {
                sender.sendMessage(NO_PERMISSION);
                return;
            }
            new ReportMenu((ProxiedPlayer) sender).open(1);
        }
    }
}
