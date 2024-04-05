package com.lothus.bungee.redis;

import com.lothus.bungee.BungeeCore;
import com.lothus.bungee.util.title.TitleUtil;
import com.lothus.core.Core;
import com.lothus.core.app.buy.Buy;
import com.lothus.core.app.update.UpdateAccount;
import com.lothus.core.discord.vincule.Vincule;
import com.lothus.core.games.GameInfo;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.network.packet.PacketServer;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.storage.redis.channels.RedisChannel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.TimeUnit;

import static com.lothus.core.app.buy.type.CategoryType.*;
import static com.lothus.core.discord.state.LinkState.PENDENTE;

public class BungeeListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        GameInfo gameInfo;
        ProxiedPlayer player;
        ServerInfo serverInfo;
        LothPlayer lothPlayer;
        PacketServer packetServer;
        Vincule vincule;
        UpdateAccount updateAccount;
        RedisChannel redisChannel = RedisChannel.valueOf(channel);

        switch (redisChannel) {
            case PLAYER_ACCOUNT_UPDATE:
                lothPlayer = Core.getGson().fromJson(message, LothPlayer.class);

                if (lothPlayer == null)return;

                Core.getPlayerController().replace(lothPlayer);
                break;
            case SERVER_START:
                serverInfo = Core.getGson().fromJson(message, ServerInfo.class);

                if (serverInfo == null) return;
                Core.getServerController().load(serverInfo);
                BungeeCore.getInstance().getServers().loadServer(serverInfo);
                break;
            case SERVER_UPDATE:
                serverInfo = Core.getGson().fromJson(message, ServerInfo.class);

                if (serverInfo == null) return;

                Core.getServerController().update(serverInfo);

                if (ProxyServer.getInstance().getServerInfo(serverInfo.getName()) == null) {
                    BungeeCore.getInstance().getServers().loadServer(serverInfo);
                }
                break;
            case SERVER_STOP:
                serverInfo = Core.getGson().fromJson(message, ServerInfo.class);

                if (serverInfo == null) return;
                Core.getServerController().unload(serverInfo.getName());
                BungeeCore.getInstance().getServers().unloadServer(serverInfo);
                break;
            case GAME_START:
                gameInfo = Core.getGson().fromJson(message, GameInfo.class);

                if (gameInfo == null)return;
                Core.getGameController().load(gameInfo);
                break;
            case GAME_UPDATE:
                gameInfo = Core.getGson().fromJson(message, GameInfo.class);

                if (gameInfo == null)return;
                Core.getGameController().update(gameInfo);
                break;
            case GAME_STOP:
                gameInfo = Core.getGson().fromJson(message, GameInfo.class);

                if (gameInfo == null)return;
                Core.getGameController().unload(gameInfo.getId());
                break;
            case DISCORD_VINCULE_ACCOUNT:
                vincule = Core.getGson().fromJson(message, Vincule.class);

                if (vincule == null)return;
                if (vincule.getState() !=PENDENTE)return;

                player = ProxyServer.getInstance().getPlayer(vincule.getNick());
                if (player == null)return;

                TextComponent t = new TextComponent("§aClique §2§lAQUI §apara vincular sua conta.");
                t.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vincular y"));

                player.sendMessage("");
                player.sendMessage("§2§lDISCORD");
                player.sendMessage("§aVocê recebeu uma solicitação de vinculo na conta §2" + vincule.getDiscordTag() + "§a.");
                player.sendMessage(t);
                player.sendMessage("");
                Core.getVinculeController().load(player.getUniqueId(), vincule);
                break;
            case APP_UPDATE_ACCOUNT:
                updateAccount = Core.getGson().fromJson(message, UpdateAccount.class);

                if (updateAccount == null)return;

                lothPlayer = Core.getPlayerController().get(updateAccount.getNickname());

                if (lothPlayer == null)return;

                lothPlayer.setCash(updateAccount.getCash());
                Core.getDataPlayer().update(lothPlayer);
                break;
            case BUY_IN_APP:
                Buy buy = Core.getGson().fromJson(message, Buy.class);

                if (buy == null)return;

                if (buy.getName() == null)return;
                if (buy.getProduct() == null)return;

                lothPlayer = Core.getDataPlayer().get(buy.getName());

                if (lothPlayer == null)return;

                player = ProxyServer.getInstance().getPlayer(lothPlayer.getUniqueId());

                if (player == null)return;

                int days = 0;

                if (TimeUnit.HOURS.toDays(buy.getHours()) >= 1) {
                    days = (int) TimeUnit.HOURS.toDays(buy.getHours());
                }

                if (buy.getType() == VIP) {
                    player.sendMessage("");
                    player.sendMessage("§b§lAPP §7-> §b§lVIP");
                    player.sendMessage("§eVocê adquiriu " + buy.getProduct() + "§e por §b§n" + (days > 0 ? days + " dias" : buy.getHours() + " horas") + "§e.");
                    player.sendMessage("");
                    TitleUtil.sendTitle(player, "§b§lAPP", "§eA sua compra foi creditada.");
                } else if (buy.getType() == CASH) {
                    player.sendMessage("");
                    player.sendMessage("§b§lAPP §7-> §b§lCASH");
                    player.sendMessage("§eVocê adquiriu o saldo de " + buy.getProduct() + "§e em cash.");
                    player.sendMessage("");
                    TitleUtil.sendTitle(player, "§b§lAPP", "§eA sua compra foi creditada.");
                } else if (buy.getType() == BOX) {
                    player.sendMessage("");
                    player.sendMessage("§b§lAPP §7-> §b§lBOX");
                    player.sendMessage("§eVocê adquiriu §b1§e caixa misteriosa.");
                    player.sendMessage("");
                    TitleUtil.sendTitle(player, "§b§lAPP", "§eA sua compra foi creditada.");
                } else if (buy.getType() == COSMETICS) {
                    player.sendMessage("");
                    player.sendMessage("§b§lAPP §7-> §b§lCOSMETIC");
                    player.sendMessage("§eVocê adquiriu §b1§e cosmético.");
                    player.sendMessage("");
                    TitleUtil.sendTitle(player, "§b§lAPP", "§eA sua compra foi creditada.");
                }
                Core.getDataPlayer().update(lothPlayer);
                Core.getPlayerController().replace(lothPlayer);
                break;
            case MAINTENANCE:
                serverInfo = Core.getGson().fromJson(message, ServerInfo.class);

                if (serverInfo == null)return;

                Core.getServerController().unload(serverInfo.getName());
                Core.getServerController().load(serverInfo);
                break;
            case PLAYER_CONNECT_SERVER:
                packetServer = Core.getGson().fromJson(message, PacketServer.class);

                if (packetServer == null)return;

                player = ProxyServer.getInstance().getPlayer(packetServer.getUniqueId());

                if (player == null)return;
                if (player.getServer().getInfo().getName().equalsIgnoreCase(packetServer.getServerName()))return;

                net.md_5.bungee.api.config.ServerInfo s = ProxyServer.getInstance().getServerInfo(packetServer.getServerName());
                if (s==null)return;

                player.connect(s);
                break;
        }
    }
}
