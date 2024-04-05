package com.lothus.bungee.commands.register.moderation.punish.evidence;

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

public class EvidenceCommand extends BungeeCommandBase {
    public EvidenceCommand() {
        super(
                "evidence"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = (ProxiedPlayer) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.MOD.ordinal())) {
            if (!lothPlayer.getGroup().containsPermission("command.evidence")) {
                player.sendMessage(NO_PERMISSION);
                return;
            }
        }

        if (args.length == 0) {
            player.sendMessage("§cSintaxe incorreta, utilize '/evidence [name] [id] [evidencia]' para continuar.");
            return;
        }

        if (args.length > 0) {
            LothPlayer lp = Core.getDataPlayer().get(args[0]);
            if (lp == null) {
                player.sendMessage("§cUsuário não encontrado.");
                return;
            }

            if (args.length > 1) {
                if (!lp.getPunishes().containsKey(args[1])) {
                    player.sendMessage("§cPunição não encontrada.");
                    return;
                }

                PunishesInfo punishesInfo = lp.getPunishes().get(args[1]);

                if (args.length > 2) {
                    if (!args[2].startsWith("https://")) {
                        player.sendMessage("§cA prova deve iniciar com 'https://'.");
                        return;
                    }
                    punishesInfo.setEvidence(args[2]);
                    player.sendMessage("§aEvidência alterada com sucesso.");
                    lothPlayer.getPunishes().replace(punishesInfo.getId(), punishesInfo);
                    if (Core.getPlayerController().get(lothPlayer.getUniqueId()) != null) {
                        Core.getPlayerController().replace(lothPlayer);
                    }
                    Core.getDataPlayer().update(lothPlayer);
                    return;
                }
                player.sendMessage("§cSintaxe incorreta, utilize '/evidence " + args[0] + " " + args[1] + " [evidencia]' para continuar.");
                return;
            }

            TextComponent t = new TextComponent("§ePunições aplicadas: \n");

            for (PunishesInfo p : lp.getPunishes().values()) {
                if (p.isExpired())continue;

                TextComponent c = new TextComponent(" §7#" + p.getId() + " - " + p.getReason().getDisplay() + "\n");
                c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eClique para selecionar.")));
                c.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/evidence " + lp.getName() + " " + p.getId() + " [evidence]"));

                t.addExtra(c);
            }

            player.sendMessage(t);
            return;
        }
    }
}
