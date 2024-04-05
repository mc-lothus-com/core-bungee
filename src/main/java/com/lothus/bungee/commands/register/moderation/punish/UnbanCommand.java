package com.lothus.bungee.commands.register.moderation.punish;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.player.stats.PunishStats;
import com.lothus.core.punish.PunishesInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UnbanCommand extends BungeeCommandBase {

    public UnbanCommand() {
        super("revogar", "unpunish");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer duzyPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
            if (!(duzyPlayer.getGroup().getRank().ordinal() <= Rank.ADMIN.ordinal())) {
                sender.sendMessage(NO_PERMISSION);
                return;
            }
        }

        if (args.length > 0) {
            if (Core.getDataPlayer().get(args[0]) == null) {
                sender.sendMessage("§cUsuário não encontrado!");
                return;
            }

            LothPlayer player = Core.getDataPlayer().get(args[0]);
            if (player.getPunishes().isEmpty()) {
                sender.sendMessage("§cNão existem punições neste usuário.");
                return;
            }

            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("ALL")) {
                    try {
                        player.getPunishes().values().forEach(p -> {
                            p.setExpired(true);
                            player.getPunishes().replace(p.getId(), p);
                        });
                        if (Core.getPlayerController().get(player.getUniqueId()) != null) {
                            Core.getPlayerController().replace(player);
                        }
                        Core.getDataPlayer().update(player);
                    } catch (Exception e) {
                        sender.sendMessage("§cNão foi possível revogar todas as punições deste usuário.");
                        return;
                    }
                    sender.sendMessage("§eTodas as punições deste usuário foram revogadas.");
                    return;
                }

                PunishesInfo punishesInfo = player.getPunishes().get(args[1]);

                if (punishesInfo == null) {
                    sender.sendMessage("§cA punição §f" + args[1] + " §cnão foi encontrada.");
                    return;
                }

                punishesInfo.setExpired(true);
                player.getPunishes().replace(punishesInfo.getId(), punishesInfo);
                if (Core.getPlayerController().get(player.getUniqueId()) != null) {
                    Core.getPlayerController().replace(player);
                }
                Core.getDataPlayer().update(player);
                sender.sendMessage("§aVocê revogou a punição §f" + punishesInfo.getId() + "§a.");

                if (sender instanceof ProxiedPlayer) {
                    if (punishesInfo.getAuthor() != null) {
                        if (punishesInfo.getAuthor().equalsIgnoreCase("CONSOLE")) {
                            LothPlayer author = Core.getPlayerController().get(punishesInfo.getAuthor()) == null ? Core.getDataPlayer().get(punishesInfo.getAuthor()) : Core.getPlayerController().get(punishesInfo.getAuthor());

                            if (author.getPunishStats() == null) {
                                author.setPunishStats(new PunishStats());
                            }

                            author.getPunishStats().setPenaltiesRevoked(author.getPunishStats().getBanPunishments() + 1);
                            Core.getDataPlayer().update(author);
                        }
                    }
                }
                return;
            }


            TextComponent textComponent = new TextComponent("§ePunições aplicadas: \n\n");

            for (PunishesInfo punishesInfo : player.getPunishes().values()) {
                if (punishesInfo.isExpired()) continue;
                TextComponent component = new TextComponent(" §8§l● §7" + punishesInfo.getType().getDisplay() + " §7- §7" + punishesInfo.getReason().getDisplay() + "\n");
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/revogar " + player.getName() + " " + punishesInfo.getId()));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("§7Clique para revogar esta punição.")}));
                textComponent.addExtra(component);
            }

            TextComponent ab = new TextComponent("\n §8§l● §eClique para revogar todas as punições.\n\n");
            ab.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/revogar " + player.getName() + " all"));
            ab.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eClique para revogar todas as punições.")));
            textComponent.addExtra(ab);

            sender.sendMessage(textComponent);
            return;
        }
        sender.sendMessage("§cSintaxe incorreta! Utilize: /revogar <usuário>");
    }


}
