package com.lothus.bungee.commands.register.moderation.punish;

import com.lothus.bungee.api.discord.DiscordWebhook;
import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.player.stats.PunishStats;
import com.lothus.core.punish.PunishesInfo;
import com.lothus.core.punish.reason.PunishReason;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.lothus.core.punish.type.PunishType.BAN;
import static com.lothus.core.punish.type.PunishType.TEMP_BAN;


public class PunirCommand extends BungeeCommandBase {

    public PunirCommand() {
        super("punir", "punish");
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

        if (args.length == 0) {
            sender.sendMessage("§cSintaxe incorreta, utilize '/punir [jogador]' para ver as punições.");
            return;
        }

        if (args.length > 0) {
            LothPlayer lothPlayer = (ProxyServer.getInstance().getPlayer(args[0]) == null ? Core.getDataPlayer().get(args[0]) : Core.getPlayerController().get(args[0]));

            if (lothPlayer == null) {
                sender.sendMessage("§cUsuário não encontrado.");
                return;
            }

            if (lothPlayer.getName().equalsIgnoreCase("ToddyDeveloper")) {
                sender.sendMessage("§cVocê não pode punir este jogador.");
                return;
            }

            if (args.length > 1) {
                PunishReason r = PunishReason.getTagByName(args[1]);

                if (r == null) {
                    sender.sendMessage("§cMotivo não encontrado.");
                    return;
                }

                PunishesInfo punishesInfo = new PunishesInfo(sender.getName(), lothPlayer.getUniqueId(), r, null);

                if (args.length > 2) {
                    String e = args[2];

                    if (!e.startsWith("https://")) {
                        sender.sendMessage("§cA prove deve começar com https://.");
                        return;
                    }
                    punishesInfo.setEvidence(e);
                }

                if (sender instanceof ProxiedPlayer) {
                    LothPlayer l = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
                    if (!(l.getGroup().getRank().ordinal() <= Rank.GER.ordinal())) {
                        if (punishesInfo.getEvidence() == null) {
                            sender.sendMessage("§cVocê deve incluir uma prova para continuar.");
                            return;
                        }
                    } else {
                        punishesInfo.setEvidence("Não informado");
                    }

                    if (l.getGroup().getRank() == Rank.TRIAL) {
                        if (punishesInfo.getType() == BAN || punishesInfo.getType() == TEMP_BAN) {
                            sender.sendMessage("§cVocê não pode banir um jogador.");
                            return;
                        }
                    }
                }

                lothPlayer.getPunishes().put(punishesInfo.getId(), punishesInfo);
                if (Core.getPlayerController().get(lothPlayer.getUniqueId()) != null) {
                    Core.getPlayerController().replace(lothPlayer);
                }
                Core.getDataPlayer().update(lothPlayer);
                log("§7[" + lothPlayer.getName() + " recebeu a punição " + r.getDisplay() + " aplicada por " + sender.getName() + "]");
                sender.sendMessage("§eVocê aplicou a punição §b§n" + r.getDisplay() + "§e em §b" + lothPlayer.getName() + "§e.");

                wb(sender.getName(), lothPlayer.getName(), punishesInfo);

                if (sender instanceof ProxiedPlayer) {
                    LothPlayer author = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
                    if (author.getPunishStats() == null) {
                        author.setPunishStats(new PunishStats());
                    }

                    if (punishesInfo.getType().name().contains("BAN")) {
                        author.getPunishStats().setBanPunishments(author.getPunishStats().getBanPunishments() + 1);
                    } else if (punishesInfo.getType().name().contains("MUTE")) {
                        author.getPunishStats().setMutePunishments(author.getPunishStats().getMutePunishments() + 1);
                    }

                    author.getPunishStats().setTotalPunishments(author.getPunishStats().getTotalPunishments() + 1);
                    Core.getDataPlayer().update(author);
                }

                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(lothPlayer.getName());

                if (p == null) return;

                if (r.getType().name().contains("MUTE")) {
                    p.sendMessage("");
                    p.sendMessage("§cVocê foi silenciado " + (r.getTimeInDays() == 99999L ? "permanentemente." : "temporariamente."));
                    p.sendMessage("§cMotivo: " + r.getDisplay());
                    p.sendMessage("");
                    return;
                }

                ServerInfo serverInfo = getServerInfo();

                if (serverInfo == null) {
                    p.disconnect(
                            "§c§lLOTHUS\n\n§cA sua conta foi suspensa " + (r.getTimeInDays() == 99999L ? "permanentemente." : "temporariamente.") + "\n"
                            + "§cMotivo: " + r.getDisplay() + "\n" +
                                    "\n§cAcha que essa punição foi aplicada injustamente?\n" +
                                    "§cConteste sua punição em: §ediscord.gg/lothus"
                    );
                    return;
                }

                p.connect(ProxyServer.getInstance().getServerInfo(serverInfo.getName()));
                return;
            }

            LothPlayer hyzePlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
            TextComponent textComponent = new TextComponent("§eMotivos disponíveis: \n \n");
            for (PunishReason reason : PunishReason.values()) {
                if (hyzePlayer.getGroup().getRank() == Rank.TRIAL) {
                    if (reason.getType().equals(BAN) || reason.getType().equals(TEMP_BAN)) {
                        continue;
                    }
                }

                TextComponent component = new TextComponent(" §8§l● §7" + reason.getDisplay() + "\n");
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                        "§fTipo de punição: §7" + reason.getType().name() + "\n§fData de expiração: §7" + (
                                reason.getType().name().startsWith("TEMP") ? new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(reason.getTimeInDays())) :
                                        "Permanente"
                        ) + "\n§eClique para selecionar.")));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/punir " + lothPlayer.getName() + " " + reason.name() + " prova"));
                textComponent.addExtra(component);
            }
            sender.sendMessage(textComponent);
            return;
        }
    }

    private void wb(String author, String infrator, PunishesInfo punishesInfo) {
        DiscordWebhook webhook = new DiscordWebhook(
                ("https://discord.com/api/webhooks/1094841781599866950/iQ_HnP2xSI7jnhP2xtPF4gr1bIhCD8EYNnF4muT423BhlEKqa1xMTRGKmZwPdZyCQZpq")
        );
        
        webhook.setUsername("Lothus - Punições");
        webhook.setTts(true);

        webhook.setAvatarUrl("https://i.imgur.com/j7biAcC.png");

        webhook.addEmbed(
                new DiscordWebhook.EmbedObject()
                        .setTitle("UMA NOVA PUNIÇÃO FOI ENCONTRADA")
                        .addField("Autor: ", author, true)
                        .addField("Infrator: ", infrator , true)
                        .addField("Motivo: ", punishesInfo.getReason().getDisplay(), false)
                        .addField("Evidência: ", punishesInfo.getEvidence(), false)
        );
        try {
            webhook.execute();
        } catch (IOException e) {
            return;
        }
    }
    
    private ServerInfo getServerInfo() {
        Comparator<ServerInfo> comparator = Comparator.comparing(ServerInfo::getPlayers);
        List<ServerInfo> list = Core.getServerController().get(ServerType.ROOM_PUNISH);
        list.sort(comparator);

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }
}
