package com.lothus.bungee.commands.register.moderation.staff;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StCommand extends BungeeCommandBase {

    public StCommand() {
        super(
                "st"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());

            if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal())) {
                sender.sendMessage(NO_PERMISSION);
                return;
            }
        }

        TextComponent textComponent = new TextComponent("§eStaffers Online:\n");
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            LothPlayer o = Core.getPlayerController().get(proxiedPlayer.getUniqueId());
            if (o.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal()) {
                TextComponent component = new TextComponent(" §7- " + o.getGroup().getRank().getColor() + "§l" + o.getGroup().getRank().getName().toUpperCase() + " "  + o.getGroup().getRank().getColor() + o.getName() + "\n");
                textComponent.addExtra(component);
            }
        }

        sender.sendMessage(textComponent);
    }
}
