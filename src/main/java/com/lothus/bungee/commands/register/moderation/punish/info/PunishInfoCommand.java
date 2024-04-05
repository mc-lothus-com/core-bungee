package com.lothus.bungee.commands.register.moderation.punish.info;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.punish.PunishesInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PunishInfoCommand extends BungeeCommandBase {

    public PunishInfoCommand() {
        super(
                "pinfo"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
            if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal())) {
                if (!lothPlayer.getGroup().containsPermission("command.pinfo")) {
                    sender.sendMessage(NO_PERMISSION);
                    return;
                }
            }
        }

        if (args.length == 0) {
            sender.sendMessage("§cSintaxe incorreta, utilize '/pinfo [nick] [id]' para continuar.");
            return;
        }

        if (args.length > 0) {
            LothPlayer lp = Core.getDataPlayer().get(args[0]);
            if (lp == null) {
                sender.sendMessage("§cUsuário não encontrado.");
                return;
            }

            if (args.length > 1) {
                if (!lp.getPunishes().containsKey(args[1])) {
                    sender.sendMessage("§cPunição não encontrada.");
                    return;
                }

                PunishesInfo punishesInfo = lp.getPunishes().get(args[1]);

                sender.sendMessage("");
                sender.sendMessage("§eId: §7#" + punishesInfo.getId());
                sender.sendMessage("§eAutor: §7" + (punishesInfo.getAuthor() == null ? "Não identificado" : punishesInfo.getAuthor()));
                sender.sendMessage("§eMotivo: §7" + punishesInfo.getReason().getDisplay());
                sender.sendMessage("§eTipo de punição: §7" + punishesInfo.getType().name());
                sender.sendMessage("§eEvidência: §7" + punishesInfo.getEvidence());
                sender.sendMessage("§eExpirado: " + (!punishesInfo.isExpired() ? "§cNão" : "§aSim"));
                sender.sendMessage("");
                return;
            }

            TextComponent t = new TextComponent("§ePunições aplicadas: \n");

            for (PunishesInfo p : lp.getPunishes().values()) {
                if (p.isExpired())continue;

                TextComponent c = new TextComponent(" §7#" + p.getId() + " - " + p.getReason().getDisplay() + "\n");
                c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eClique para selecionar.")));
                c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pinfo " + lp.getName() + " " + p.getId()));

                t.addExtra(c);
            }

            sender.sendMessage(t);
            return;
        }
    }
}
