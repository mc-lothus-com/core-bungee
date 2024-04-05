package com.lothus.bungee.commands;

import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public abstract class BungeeCommandBase extends Command {

    public boolean enabled = true;
    public static final String NO_PERMISSION = "§cVocê não possui permissão para utilizar este comando!";

    public BungeeCommandBase(String name) {
        super(name);
    }

    public BungeeCommandBase(String name, String... aliases) {
        super(name, null, aliases);
    }

    public abstract void execute(CommandSender sender, String[] args);

    public Integer getInteger(String string) {
        return Integer.valueOf(string);
    }

    public boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void log(String message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
            if (lothPlayer.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal()) {
                player.sendMessage(message);
            }
        }
    }


    public String getArgs(String[] args, int starting) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = starting; i < args.length; i++) {
            stringBuilder.append(args[i] + " ");
        }
        return stringBuilder.toString().substring(0, stringBuilder.length() - 1);
    }

}

