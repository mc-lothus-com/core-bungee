package com.lothus.bungee.commands.register.moderation.staff;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.servers.ServerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

public class StaffCommand extends BungeeCommandBase {

    private HashMap<UUID, Integer> countMessages = new HashMap<>();

    public StaffCommand() {
        super("s", "sc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(proxiedPlayer.getUniqueId());
        if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal())) {
            proxiedPlayer.sendMessage(NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            proxiedPlayer.sendMessage("§cSintaxe incorreta, utilize '/s [mensagem]'.");
            return;
        }

        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("ativar")) {
                if (lothPlayer.getPrefs().isStaffChat()) {
                    proxiedPlayer.sendMessage("§cO chat staff já está ativado.");
                    return;
                }

                lothPlayer.getPrefs().setStaffChat(true);
                countMessages.remove(proxiedPlayer.getUniqueId());
                proxiedPlayer.sendMessage("§aVocê ativou o chat staff.");
                Core.getDataPlayer().update(lothPlayer);
                return;
            }

            if (args[0].equalsIgnoreCase("desativar")) {
                if (!lothPlayer.getPrefs().isStaffChat()) {
                    proxiedPlayer.sendMessage("§cO chat staff já está desativado.");
                    return;
                }

                lothPlayer.getPrefs().setStaffChat(false);
                proxiedPlayer.sendMessage("§cVocê desativou o chat staff.");
                Core.getDataPlayer().update(lothPlayer);
                return;
            }

            if (!lothPlayer.getPrefs().isStaffChat()) {
                proxiedPlayer.sendMessage("§cO seu chat staff está desativado!");
                return;
            }

            String message = "";
            String[] arrayOfString = args;
            int i = args.length;

            String s;
            for (byte b = 0; b < i; ++b) {
                s = arrayOfString[b];
                if (message != "") {
                    message = String.valueOf(String.valueOf(String.valueOf(message))) + " ";
                }

                message = String.valueOf(String.valueOf(String.valueOf(message))) + s;
            }

            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                LothPlayer received = Core.getPlayerController().get(player.getUniqueId());
                if (received.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal()) {
                    if (received.getPrefs().isStaffChat()) {
                        ServerInfo serverInfo = Core.getServerController().get(proxiedPlayer.getServer().getInfo().getName());
                        if (serverInfo != null) {
                            player.sendMessage("§d§l[S] §7[" + serverInfo.getType().getName()  + serverInfo.getName().split("-")[1] + "] " + lothPlayer.getGroup().getTag().getColor() + "§l" + lothPlayer.getGroup().getTag().getName().toUpperCase() + " " + lothPlayer.getGroup().getTag().getColor() + lothPlayer.getName() + ": §7" + message);
                        } else {
                            player.sendMessage("§d§l[S] " + lothPlayer.getGroup().getTag().getColor() + "§l" + lothPlayer.getGroup().getTag().getName().toUpperCase() + " " + lothPlayer.getGroup().getTag().getColor() + lothPlayer.getName() + ": §7" + message);
                        }
                    } else {
                        if (countMessages.containsKey(player.getUniqueId())) {
                            countMessages.replace(player.getUniqueId(), countMessages.get(player.getUniqueId()) + 1);
                        } else {
                            countMessages.put(player.getUniqueId(), 1);
                        }

                        TextComponent textComponent = new TextComponent("§eVocê perdeu §b");
                        TextComponent count = new TextComponent(countMessages.get(player.getUniqueId()) + " " + (countMessages.get(player.getUniqueId()) == 1 ? "mensagem" : "mensagens"));
                        TextComponent textComponent1 = new TextComponent(" §edo chat da equipe.");

                        count.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eClique para ativar o chat da equipe.")));
                        count.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/s ativar"));

                        textComponent.addExtra(count);
                        textComponent.addExtra(textComponent1);
                        player.sendMessage(textComponent);
                    }
                }
            }
        }


    }
}
