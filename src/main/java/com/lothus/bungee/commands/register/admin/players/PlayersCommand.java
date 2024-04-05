package com.lothus.bungee.commands.register.admin.players;

import com.lothus.bungee.BungeeCore;
import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.servers.ServerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayersCommand extends BungeeCommandBase {

    public PlayersCommand() {
        super("players");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());

            if (lothPlayer.getGroup().getRank() != Rank.CEO) {
                if (!lothPlayer.getGroup().containsPermission("command.maxplayers")) {
                    sender.sendMessage(NO_PERMISSION);
                    return;
                }
            }
        }

        if (args.length == 0) {
            sender.sendMessage("§cSintaxe incorreta, utilize '/players [servidor] [quantidade]'.");
            return;
        }

        if (args.length > 0) {
            if (isInteger(args[0])) {
                try {
                    Core.getServerInfo().getConfiguration().setMaxPlayers(Integer.parseInt(args[0]));
                    File file = new File(BungeeCore.getInstance().getDataFolder(), "config.yml");
                    Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(BungeeCore.getInstance().getDataFolder(), "config.yml"));

                    config.set("server.maxPlayers", Integer.parseInt(args[0]));

                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
                    sender.sendMessage("§aMáximo de jogadores do servidor alterado para " + args[0] + ".");
                    Core.getDataServer().update(Core.getServerInfo());
                    return;
                } catch (IOException e) {
                    sender.sendMessage("§cNão foi possível atualizar o máximo de jogadores.");
                    return;
                }
            }

            ServerInfo serverInfo = Core.getServerController().get(args[0]);

            if (serverInfo == null) {
                sender.sendMessage("§cServidor não encontrado.");
                return;
            }

            if (args.length > 1) {
                try {
                    int maxPlayers = Integer.parseInt(args[1]);
                    serverInfo.getConfiguration().setMaxPlayers(maxPlayers);

                    Core.getDataServer().update(serverInfo);
                    sender.sendMessage("§aMáximo de jogadores do servidor " + serverInfo.getName() + " alterado para " + maxPlayers + ".");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cQuantidade inválida.");
                }
            } else {
                sender.sendMessage("§aMáximo de jogadores do servidor " + serverInfo.getName() + " é " + serverInfo.getConfiguration().getMaxPlayers() + ".");
            }
        }
    }
}
