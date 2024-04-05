package com.lothus.bungee.commands.register.player.discord;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.discord.state.LinkState;
import com.lothus.core.discord.vincule.Vincule;
import com.lothus.core.player.LothPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

import static com.lothus.core.storage.redis.channels.RedisChannel.DISCORD_VINCULE_ACCOUNT;

public class VinculeCommand extends BungeeCommandBase {

    private HashMap<UUID, Long> cooldown = new HashMap<>();

    public VinculeCommand() {
        super("vincule", "vincule.command", "vincule", "vincule", "vincular", "discord");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer)sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (args.length == 0) {
            player.sendMessage("§cSintaxe incorreta, utilize '/vincular [y/n]' para aceitar ou recusar a vinculação.");
            return;
        }

        String arg = args[0].toLowerCase();
        if (arg == "n") {
            player.sendMessage("§cVinculação recusada.");
            Core.getVinculeController().unload(player.getUniqueId());
            return;
        }

        Vincule vincule = Core.getVinculeController().get(player.getUniqueId());

        if (vincule == null) {
            player.sendMessage("§cNão há vinculação pendente.");
            return;
        }

        lothPlayer.getSocial().setDiscord(vincule.getId());
        vincule.setState(LinkState.VINCULADO);
        player.sendMessage("§eVinculação realizada com sucesso!");
        Core.getDataPlayer().update(lothPlayer);
        Core.getRedis().message(DISCORD_VINCULE_ACCOUNT.name(), Core.getGson().toJson(vincule));
        Core.getVinculeController().unload(player.getUniqueId());
        return;
    }
}
