package com.lothus.bungee.commands.register.player.aliases;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class LobbyCommand extends BungeeCommandBase {

    private List<UUID> confirm = new ArrayList<>();

    public LobbyCommand() {
        super("lobby", "l", "hub", "lb");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§cApenas jogadores podem utilizar este comando!");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        ServerInfo serverInfo = Core.getServerController().get(player.getServer().getInfo().getName());
        if (serverInfo.getType() == ServerType.LOGIN) {
            player.sendMessage("§cVocê não pode fazer isso aqui!");
            return;
        }
        if (serverInfo.getType().equals(ServerType.LOBBY)) {
            player.sendMessage(
                    "§cVocê já está no lobby!"
            );
            return;
        }

        if (lothPlayer.getPrefs().isLobby()) {
            if (!confirm.contains(player.getUniqueId())) {
                player.sendMessage(
                        "§eVocê tem certeza disso? Digite /lobby para confirmar."
                );
                confirm.add(player.getUniqueId());
                return;
            }
        }

        ServerType type = (serverInfo.getType().equals(ServerType.ROOM_SKYWARS) ? ServerType.LOBBY_SKYWARS : serverInfo.getType().equals(ServerType.ROOM_BEDWARS) ? ServerType.LOBBY_BEDWARS : ServerType.LOBBY);
        Comparator<ServerInfo> comparator = Comparator.comparing(ServerInfo::getPlayers);
        List<ServerInfo> list = Core.getServerController().get(type);
        list.sort(comparator);

        if (list.isEmpty()) {
            player.sendMessage("§cNão há servidores disponíveis!");
            return;
        }
        ServerInfo s = list.get(0);

        if (s == null) {
            player.sendMessage("§cNão há servidores disponíveis!");
            return;
        }

        player.connect(ProxyServer.getInstance().getServerInfo(s.getName()));
        confirm.remove(player.getUniqueId());
    }
}
