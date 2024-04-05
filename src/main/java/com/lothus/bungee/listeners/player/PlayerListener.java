package com.lothus.bungee.listeners.player;

import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.player.skin.Skin;
import com.lothus.core.player.social.fake.Fake;
import com.lothus.core.servers.status.ServerStatus;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler(priority = 64)
    public void onPreLogin(PreLoginEvent event) {
        PendingConnection con = event.getConnection();
        con.setOnlineMode(check(con.getName()));
    }

    @EventHandler
    public void onJoin(LoginEvent event) {
        PendingConnection con = event.getConnection();

        LothPlayer lothPlayer = Core.getDataPlayer().get(con.getUniqueId());

        if (lothPlayer == null) {
            lothPlayer = new LothPlayer(con.getUniqueId(), con.getName());
            lothPlayer.setPremium(con.isOnlineMode());
            Core.getDataPlayer().create(lothPlayer);
        }

        if (lothPlayer.getSkin() == null) {
            lothPlayer.setSkin(new Skin(con.getName(), con.getUniqueId(), "", ""));
        }

        Core.getPlayerController().load(lothPlayer);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer con = event.getPlayer();
        LothPlayer lothPlayer = Core.getPlayerController().get(event.getPlayer().getUniqueId());
        if (Core.getServerInfo().getStatus() == ServerStatus.MAINTENANCE_MODE) {
            if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal())) {
                if (!lothPlayer.getGroup().containsPermission("maintenance.bypass")) {
                    con.disconnect("§cNossos servidores estão em manutenção no momento.\n§cTente novamente mais tarde.\n\n§cMais informações em: §ediscord.gg/lothus\n");
                    return;
                }
            }
        }

        if (Core.getServerInfo().getStatus() == ServerStatus.BETA_MODE) {
            if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.BETA.ordinal())) {
                if (!lothPlayer.getGroup().containsPermission("beta.bypass")) {
                    con.disconnect("§cNossos servidores estão em beta no momento.\n§cAdquira BETA e tenha acesso ao servidor em: \n§emc-lothus.com\n\n§cMais informações em: §ediscord.gg/lothus\n");
                    return;
                }
            }
        }

        if (ProxyServer.getInstance().getOnlineCount() >= Core.getServerInfo().getConfiguration().getMaxPlayers()) {
            if (lothPlayer.getGroup().getRank() == Rank.MEMBRO) {
                con.disconnect("§cNossos servidores estão lotados no momento.\n§cTente novamente mais tarde.\n\n§cAdquira VIP e acesse o servidor mesmo cheio:\n§ewww.mc-lothus.com/loja\n");
                return;
            }
        }

        if (lothPlayer.getSocial().getLastServer() == null) {
            lothPlayer.getSocial().setLastServer("LOBBY");
        }

        if (lothPlayer.getName() != con.getName()) {
            lothPlayer.setName(con.getName());
        }

        if (lothPlayer.getSocial().getFake() != null && !lothPlayer.getSocial().getFake().getName().equalsIgnoreCase(lothPlayer.getName())) {
            lothPlayer.getSocial().setFake(new Fake(lothPlayer.getName(), lothPlayer.getGroup().getRank()));
        }

        Core.getDataPlayer().update(lothPlayer);
        Core.getServerInfo().setPlayers(ProxyServer.getInstance().getOnlineCount());
        Core.getDataServer().update(Core.getServerInfo());
    }

    @EventHandler(priority = 64)
    public void onQuit(PlayerDisconnectEvent event) {
        LothPlayer lothPlayer = Core.getPlayerController().get(event.getPlayer().getUniqueId());

        if (lothPlayer == null) {
            return;
        }

        Core.getPlayerController().unload(event.getPlayer().getUniqueId());
        Core.getServerInfo().setPlayers(ProxyServer.getInstance().getOnlineCount()-1);
        Core.getDataServer().update(Core.getServerInfo());
    }

    private boolean check(String name) {
        boolean premium = false;
        UUID uniqueId = null;

        try {
            uniqueId = Core.getUniqueIdFetcher().getUUID(name);
            premium = (uniqueId != null);
        } catch (Exception ignored) {}
        return premium;
    }
}
