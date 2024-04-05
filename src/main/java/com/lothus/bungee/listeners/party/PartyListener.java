package com.lothus.bungee.listeners.party;

import com.lothus.bungee.BungeeCore;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.party.Party;
import com.lothus.core.servers.ServerInfo;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PartyListener implements Listener {

    @EventHandler
    public void onServerConnect(ServerConnectedEvent event) {
        if (event.getServer() == null)return;

        ProxiedPlayer player = event.getPlayer();

        Party party = Core.getPartyController().get(player.getUniqueId());

        if (party == null)return;
        if (!party.isLeader(player.getUniqueId()))return;

        ServerInfo serverInfo = Core.getServerController().get(event.getServer().getInfo().getName());

        if (serverInfo == null)return;
        if (!serverInfo.getType().name().startsWith("ROOM_"))return;

        for (ProxiedPlayer y : ProxyServer.getInstance().getPlayers()) {
            if (!party.hasMember(y.getUniqueId()))continue;
            if (y == player)continue;

            if (y.getServer() == event.getServer())continue;

            y.connect(event.getServer().getInfo());
        }
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (event.getServer() ==null)return;

        ServerInfo serverInfo = Core.getServerController().get(event.getServer().getInfo().getName());

        if (serverInfo ==null)return;
        if (!(serverInfo.getType().name().startsWith("LOBBY")))return;

        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        if (lothPlayer == null)return;
        lothPlayer.getSocial().setLastServer(serverInfo.getType().name());
        Core.getDataPlayer().update(lothPlayer);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Party party = Core.getPartyController().get(uuid);

        if (party == null)return;
        if (!party.isLeader(uuid)) {
            party.remove(uuid);
            party.removeRequest(uuid);

            if (party.getMembers().isEmpty()) {
                ProxiedPlayer leader = BungeeCore.getInstance().getProxy().getPlayer(party.getLeader());

                Core.getDataParty().del(uuid);
                Core.getPartyController().unload(party);
                leader.sendMessage("§d[PARTY] §eA party foi desfeita por não conter jogadores.");
            }
        } else {
            party.getMembers().forEach(m -> {
                ProxiedPlayer player = BungeeCore.getInstance().getProxy().getPlayer(m);
                player.sendMessage("§d[PARTY] §eO líder da party desconectou, portanto a party foi desfeita.");
                Core.getPartyController().unload(party);
                Core.getDataParty().del(uuid);
            });
        }
        Core.getDataParty().del(uuid);
    }
}
