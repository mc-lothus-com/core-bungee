package com.lothus.bungee.listeners.server;

import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.punish.PunishesInfo;
import com.lothus.core.punish.type.PunishType;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import com.lothus.engines.sync.platform.Platform;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.lothus.core.servers.type.ServerType.*;

public class ServerListener implements Listener {

    @EventHandler
    public void onKick(ServerKickEvent e) {
        ProxiedPlayer player = e.getPlayer();
        if (player.getServer() == null) {
            return;
        }
        if (e.getKickedFrom().getName().contains("login") || e.getKickedFrom().getName().contains("auth") || e.getKickedFrom().getName().contains("punish"))
            return;

        Comparator<ServerInfo> comparator = Comparator.comparing(ServerInfo::getPlayers);
        List<ServerInfo> list = Core.getServerController().get((e.getKickedFrom().getName().contains("lsw-") ? LOBBY : e.getKickedFrom().getName().contains("sw-") ? ServerType.LOBBY_SKYWARS : e.getKickedFrom().getName().contains("lbw-") ? LOBBY : e.getKickedFrom().getName().contains("bw-") ? ServerType.LOBBY_BEDWARS : LOBBY));
        list.sort(comparator);

        if (list.isEmpty()) {
            player.disconnect("§cNossos servidores de reconexão estão indisponíveis no momento.");
            return;
        }

        ServerInfo connectTo = list.get(0);

        if (connectTo == null) {
            player.disconnect("§cNossos servidores de reconexão estão indisponíveis no momento.");
            return;
        }

        if (player.getServer() == null) {
            return;
        }

        e.setCancelled(true);

        if (player.getServer().getInfo().getName().equalsIgnoreCase(connectTo.getName())) {
            return;
        }

        e.setCancelServer(ProxyServer.getInstance().getServerInfo(connectTo.getName()));
    }


    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (event.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
            LothPlayer lothPlayer = Core.getPlayerController().get(event.getPlayer().getUniqueId());

            if (lothPlayer == null) {
                player.disconnect("§cNão foi possível recuperar sua conta.\n§cTente novamente.");
                return;
            }

            for (PunishesInfo punishesInfo : lothPlayer.getPunishes().values()) {
                if (!punishesInfo.isExpired()) {
                    if ((punishesInfo.getExpires() > System.currentTimeMillis()) || punishesInfo.getExpires() == -1L) {
                        Core.getPunishController().load(punishesInfo);
                        if (punishesInfo.getType() == PunishType.TEMP_BAN || punishesInfo.getType() == PunishType.BAN) {
                            ServerInfo serverInfo = getServerInfo(ROOM_PUNISH);
                            if (punishesInfo.getDeleteStsts() != -1L && punishesInfo.getDeleteStsts() <= System.currentTimeMillis()) {
                                Platform.deleteStats(player.getUniqueId());
                            }

                            if (serverInfo == null) {
                                player.disconnect(
                                        "\n§c§lLOTHUS MC\n\n§cA sua conta está suspensa!\n\n§cMotivo: §e" + punishesInfo.getReason().getDisplay() + "\n\n§cData de expiração: §e" + (punishesInfo.getReason().getTimeInDays() == 99999L ? "Nunca" : punishesInfo.getReason().getTimeInDays() + " dias") + "\n\n§cID de identificação: §e" + punishesInfo.getId() + "\n\n§cMais informações em: §ediscord.gg/lothus\n"
                                );
                                return;
                            }


                            event.setTarget(ProxyServer.getInstance().getServerInfo(serverInfo.getName()));
                            return;
                        }
                    } else {
                        punishesInfo.setExpired(true);
                    }
                }
            }

            ServerType type = (lothPlayer.isPremium() ? LOBBY : LOGIN);

            ServerInfo serverInfo = getServerInfo(type);

            if (serverInfo == null) {
                player.disconnect("§cNossos servidores estão indisponíveis no momento.");
                return;
            }

            for (UUID uuid : lothPlayer.getSocial().getFriends()) {
                ProxiedPlayer l = ProxyServer.getInstance().getPlayer(uuid);
                if (l != null) {
                    LothPlayer a = Core.getPlayerController().get(uuid);
                    player.sendMessage("§d[AMIGO] " + a.getGroup().getTag().getColor() + a.getName() + "§e está online.");
                    l.sendMessage("§d[AMIGO] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e entrou.");
                }
            }
            event.setTarget(ProxyServer.getInstance().getServerInfo(serverInfo.getName()));
            return;
        }
    }

    @EventHandler
    public void onCommand(ChatEvent event) {
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
        if (sender.getServer().getInfo().getName().startsWith("login"))return;
        if (event.getMessage().startsWith("/"))return;

        LothPlayer lothPlayer = Core.getPlayerController().get(sender.getUniqueId());
        List<PunishesInfo> pI = Core.getPunishController().getMutes(lothPlayer);

        if (pI.isEmpty())return;

        PunishesInfo punishesInfo = pI.get(0);

        if (punishesInfo == null)return;
        if (punishesInfo.getReason() == null)return;
        if (punishesInfo.isExpired())return;

        event.setCancelled(true);

        sender.sendMessage("");
        sender.sendMessage("§cVocê está silenciado " + (punishesInfo.getReason().getTimeInDays() == 99999L ? "permanentemente." : "temporariamente."));
        sender.sendMessage("§cID de identificação: " + punishesInfo.getId());
        sender.sendMessage("§cMotivo: " + punishesInfo.getReason().getDisplay());
        sender.sendMessage("");
        return;
    }

    private ServerInfo getServerInfo(ServerType type) {
        Comparator<ServerInfo> comparator = Comparator.comparing(ServerInfo::getPlayers);
        List<ServerInfo> list = Core.getServerController().get(type);
        list.sort(comparator);

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }
}
