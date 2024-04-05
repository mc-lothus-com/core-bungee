package com.lothus.bungee.commands.register.admin.server;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.status.ServerStatus;
import com.lothus.core.servers.type.ServerType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Comparator;
import java.util.List;

public class ServerInfoCommand extends BungeeCommandBase {

    public ServerInfoCommand() {
        super(
                "serverinfo"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

            if (lothPlayer.getGroup().getRank() != Rank.CEO) {
                if (!lothPlayer.getGroup().containsPermission("command.serverinfo")) {
                    player.sendMessage(NO_PERMISSION);
                    return;
                }
            }
        }

        if (args.length == 0) {
            sender.sendMessage("§cSintaxe incorreta, utilize '/serverinfo [name/list]' para continuar.");
            return;
        }

        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("list")) {
                if (args.length > 1) {
                    ServerType serverType = ServerType.getByName(args[1]);

                    if (serverType == null) {
                        sender.sendMessage("§cO serverType informado não existe.");
                        return;
                    }

                    Comparator<ServerInfo> comparator = Comparator.comparing(ServerInfo::getId);
                    List<ServerInfo> list = Core.getServerController().get(serverType);
                    list.sort(comparator);

                    if (list.isEmpty()) {
                        sender.sendMessage("§cA lista de servidores (" + serverType.name() + ") está vazia.");
                        return;
                    }

                    TextComponent textComponent = new TextComponent("§eServidores do tipo " + serverType.name() + ":");

                    for (ServerInfo s : list) {
                        TextComponent component = new TextComponent("\n§7" + s.getType() + s.getId());
                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Id: §e" + s.getId() + "\n§7Nome: §e" + s.getName() + "\n§7Players: §e" + s.getPlayers() + "\n§7Max. Players: §e" + s.getConfiguration().getMaxPlayers())));
                        textComponent.addExtra(component);
                    }
                    sender.sendMessage(textComponent);
                    return;
                }
                sender.sendMessage("§cSintaxe incorreta, utilize '/serverinfo list [type]' para continuar.");
                return;
            }

            ServerInfo serverInfo = (args[0].equalsIgnoreCase("proxy") ? Core.getServerInfo() : Core.getServerController().get(args[0]));

            if (serverInfo == null) {
                sender.sendMessage("§cServidor não encontrado.");
                return;
            }

            sender.sendMessage("");
            sender.sendMessage("§7Id: §e" + serverInfo.getId());
            sender.sendMessage("§7Nome: §e" + serverInfo.getName());
            sender.sendMessage("§7Tipo: §e" + serverInfo.getType().name().replace("#", ""));
            sender.sendMessage("§7Players: §e" + serverInfo.getPlayers());
            sender.sendMessage("§7Max. Players: §e" + serverInfo.getConfiguration().getMaxPlayers());
            sender.sendMessage("§7Estado: " + (serverInfo.getStatus() == ServerStatus.MAINTENANCE_MODE ? "§cManutenção" : "§aDisponível"));
            sender.sendMessage("");
            return;
        }
    }
}
