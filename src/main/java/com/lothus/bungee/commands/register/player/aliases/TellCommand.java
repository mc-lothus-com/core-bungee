package com.lothus.bungee.commands.register.player.aliases;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.punish.PunishesInfo;
import com.lothus.core.punish.type.PunishType;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TellCommand extends BungeeCommandBase {

    public TellCommand() {
        super("tell");
    }

    @Getter private static HashMap<UUID, UUID> conversation = new HashMap<>();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cApenas jogadores podem utilizar este comando!");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        if (args.length >= 2) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cJogador não encontrado!");
                return;
            }
            if (!lothPlayer.getPrefs().isTell()) {
                player.sendMessage("§cVocê não pode enviar mensagens privadas!");
                return;
            }
            if (target.equals(player)) {
                player.sendMessage("§cVocê não pode enviar mensagens para si mesmo!");
                return;
            }
            LothPlayer p = Core.getPlayerController().get(target.getUniqueId());
            if (!p.getPrefs().isTell()) {
                player.sendMessage("§cEste jogador não aceita mensagens privadas!");
                return;
            }

            for (PunishesInfo punishesInfo : lothPlayer.getPunishes().values()) {
                if (punishesInfo.isExpired())continue;
                if (!(punishesInfo.getExpires() < System.currentTimeMillis())) {
                    if (punishesInfo.getType().equals(PunishType.MUTE)) {
                        sender.sendMessage("");
                        sender.sendMessage("§cVocê está mutado permanentemente.");
                        sender.sendMessage("§cMotivo: " + punishesInfo.getReason().getDisplay());
                        sender.sendMessage("§cProva: " + punishesInfo.getEvidence());
                        sender.sendMessage("");
                        return;
                    } else if (punishesInfo.getType().equals(PunishType.TEMP_MUTE)) {
                        sender.sendMessage("");
                        sender.sendMessage("§cVocê está mutado temporariamente.");
                        sender.sendMessage("§cMotivo: " + punishesInfo.getReason().getDisplay());
                        sender.sendMessage("§cProva: " + punishesInfo.getEvidence());
                        sender.sendMessage("§cData de expiração: §e" + new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(punishesInfo.getExpires()));
                        sender.sendMessage("");
                        return;
                    }
                }
            }

            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) message.append(" ");
                message.append(args[i]);
            }
            player.sendMessage("§8[Mensagem para " + p.getGroup().getTag().getColor() + target.getName() + "§8] §7" + message.toString());
            target.sendMessage("§8[Mensagem de " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§8] §7" + message.toString());
            getConversation().put(player.getUniqueId(), target.getUniqueId());
            getConversation().put(target.getUniqueId(), player.getUniqueId());
            return;
        }
        player.sendMessage("§cSintaxe incorreta, utilize '/tell [jogador] [mensagem]'.");
    }

    public Iterable<String> onTabComplete(CommandSender cs, String[] args) {
        Set<String> match = new HashSet<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                if (player.getName().toLowerCase().startsWith(search)) {
                    match.add(player.getName());
                }
            }
        }
        return match;
    }
}
