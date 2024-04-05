package com.lothus.bungee.commands.register.player.discord;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnVinculeCommand extends BungeeCommandBase {


    private List<UUID> confirm = new ArrayList<>();

    public UnVinculeCommand() {
        super("unvincule", "vincule.command", "desvincular");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (lothPlayer.getSocial().getDiscord() == -1L) {
            player.sendMessage("§cVocê não está vinculado a nenhuma conta do discord.");
            return;
        }

        if (!confirm.contains(player.getUniqueId())) {
            TextComponent textComponent = new TextComponent("§eClique ");
            TextComponent here = new TextComponent("§6§lAQUI");
            TextComponent textComponent1 = new TextComponent(" §epara desvincular.");

            here.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unvincule"));

            textComponent.addExtra(here);
            textComponent.addExtra(textComponent1);

            player.sendMessage("");
            player.sendMessage("§eVocê tem certeza que deseja desvincular sua conta do discord?");
            player.sendMessage(textComponent);
            player.sendMessage("");
            confirm.add(player.getUniqueId());
            return;
        }

        lothPlayer.getSocial().setDiscord(-1L);

        if (lothPlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal()) {
            lothPlayer.getGroup().setRank(Rank.MEMBRO);
            lothPlayer.getGroup().setTag(Rank.MEMBRO);
            lothPlayer.getGroup().setLastModified(System.currentTimeMillis());
        }

        player.sendMessage("§aVocê desvinculou com sucesso.");
        Core.getDataPlayer().update(lothPlayer);
        confirm.remove(player.getUniqueId());
    }
}
