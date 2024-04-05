package com.lothus.bungee.commands.register.player.friends;

import com.lothus.bungee.BungeeCore;
import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.servers.ServerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class FriendCommand extends BungeeCommandBase {

    public FriendCommand() {
        super("friend", "", "amigo", "friends");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer))return;

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(proxiedPlayer.getUniqueId());

        if (args.length == 0) {
            proxiedPlayer.sendMessage("");
            proxiedPlayer.sendMessage("§eAjuda - Amigos");
            proxiedPlayer.sendMessage("");
            proxiedPlayer.sendMessage("§3/amigo [jogador] §8- §3Enviar um pedido de amizade.");
            proxiedPlayer.sendMessage("§3/amigo aceitar [jogador] §8- §3Aceitar um pedido de amizade.");
            proxiedPlayer.sendMessage("§3/amigo recusar [jogador] §8- §3Recusar um pedido de amizade.");
            proxiedPlayer.sendMessage("§3/amigo assistir [jogador] §8- §3Assistir partida de um jogador.");
            proxiedPlayer.sendMessage("§3/amigo listar §8- §3Ver lista de amigos.");
            proxiedPlayer.sendMessage("§3/amigo pedidos §8- §3Ver lista de pedidos.");
            proxiedPlayer.sendMessage("");
            return;
        }

        if (args.length > 0) {
            String s = args[0];
            if (s.equalsIgnoreCase("aceitar")) {
                if (args.length > 1) {
                    ProxiedPlayer ta = BungeeCore.getInstance().getProxy().getPlayer(args[1]);
                    LothPlayer target = (ta != null ? Core.getPlayerController().get(ta.getUniqueId()) : Core.getDataPlayer().get(args[1]));
                    if (target == null) {
                        proxiedPlayer.sendMessage("§cUsuário não encontrado.");
                        return;
                    }

                    if (!lothPlayer.getSocial().hasRequest(target.getUniqueId())) {
                        proxiedPlayer.sendMessage("§cVocê não tem uma solicitação de amizade deste jogador.");
                        return;
                    }

                    if (lothPlayer.getSocial().hasFriend(target.getUniqueId())) {
                        proxiedPlayer.sendMessage("§cVocê já é amigo deste jogador.");
                        return;
                    }

                    lothPlayer.getSocial().getFriends().add(target.getUniqueId());
                    target.getSocial().getFriends().add(lothPlayer.getUniqueId());
                    lothPlayer.getSocial().removeRequest(target.getUniqueId());
                    target.getSocial().removeRequest(lothPlayer.getUniqueId());

                    proxiedPlayer.sendMessage("");
                    proxiedPlayer.sendMessage("§aVocê aceitou o pedido de amizade de §f" + target.getGroup().getTag().getColor() + target.getName() + "§a.");
                    proxiedPlayer.sendMessage("");

                    if (ta != null) {
                        ta.sendMessage("");
                        ta.sendMessage("§aO jogador §f" + lothPlayer.getGroup().getTag().getColor() + lothPlayer.getName() + "§a aceitou seu pedido de amizade.");
                        ta.sendMessage("");
                    }
                    Core.getDataPlayer().update(lothPlayer);
                    Core.getDataPlayer().update(target);
                    return;
                }
                proxiedPlayer.sendMessage("§cSintaxe incorreta, utilize '/amigo aceitar [jogador]'.");
                return;
            }

            if (s.equalsIgnoreCase("recusar")) {
                if (args.length > 1) {
                    ProxiedPlayer ta = BungeeCore.getInstance().getProxy().getPlayer(args[1]);
                    LothPlayer target = (ta != null ? Core.getPlayerController().get(ta.getUniqueId()) : Core.getDataPlayer().get(args[1]));
                    if (target == null) {
                        proxiedPlayer.sendMessage("§cUsuário não encontrado.");
                        return;
                    }

                    if (!lothPlayer.getSocial().hasRequest(target.getUniqueId())) {
                        proxiedPlayer.sendMessage("§cVocê não tem uma solicitação de amizade deste jogador.");
                        return;
                    }

                    if (!lothPlayer.getSocial().hasFriend(target.getUniqueId())) {
                        proxiedPlayer.sendMessage("§cVocê já é amigo deste jogador.");
                        return;
                    }

                    lothPlayer.getSocial().removeRequest(target.getUniqueId());
                    target.getSocial().removeRequest(lothPlayer.getUniqueId());

                    proxiedPlayer.sendMessage("");
                    proxiedPlayer.sendMessage("§cO jogador §f" + lothPlayer.getGroup().getTag().getColor() + lothPlayer.getName() + "§c recusou seu pedido de amizade");
                    proxiedPlayer.sendMessage("");

                    if (ta != null) {
                        ta.sendMessage("");
                        ta.sendMessage("§cVocê recusou o pedido de amizade de §f" + target.getGroup().getTag().getColor() + target.getName() + "§c.");
                        ta.sendMessage("");
                    }
                    Core.getDataPlayer().update(lothPlayer);
                    Core.getDataPlayer().update(target);
                    return;
                }
                proxiedPlayer.sendMessage("§cSintaxe incorreta, utilize '/amigo recusar [jogador]'.");
                return;
            }

            if (s.equalsIgnoreCase("listar")) {
                if (lothPlayer.getSocial().getFriends().isEmpty()) {
                    proxiedPlayer.sendMessage("§cVocê não tem amigos.");
                    return;
                }

                for (UUID uuid : lothPlayer.getSocial().getFriends()) {
                    ProxiedPlayer ta = BungeeCore.getInstance().getProxy().getPlayer(uuid);
                    LothPlayer a = (ta != null ? Core.getPlayerController().get(uuid) : Core.getDataPlayer().get(uuid));
                    if (a == null)continue;

                    TextComponent t = new TextComponent("§7- §f" + a.getGroup().getTag().getColor() + a.getName() + "§e está " + (ta == null ? "§coffline§e." : "§aonline§e."));
                    proxiedPlayer.sendMessage(t);
                }
                return;
            }

            if (s.equalsIgnoreCase("pedidos")) {
                if (lothPlayer.getSocial().getRequests().isEmpty()) {
                    proxiedPlayer.sendMessage("§cVocê não tem pedidos de amizade.");
                    return;
                }

                proxiedPlayer.sendMessage("");
                for (UUID uuid : lothPlayer.getSocial().getRequests()) {
                    ProxiedPlayer ta = BungeeCore.getInstance().getProxy().getPlayer(uuid);
                    LothPlayer a = (ta != null ? Core.getPlayerController().get(uuid) : Core.getDataPlayer().get(uuid));
                    TextComponent t = new TextComponent("§7- " + a.getGroup().getTag().getColor() + a.getName() + " §eenviou um pedido de amizade.");
                    proxiedPlayer.sendMessage(t);
                }
                return;
            }

            if (s.equalsIgnoreCase("assistir")) {
                if (args.length > 1) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                    if (target == null) {
                        proxiedPlayer.sendMessage("§cUsuário não encontrado.");
                        return;
                    }

                    ServerInfo serverInfo = Core.getServerController().get(target.getServer().getInfo().getName());
                    ServerInfo sI = Core.getServerController().get(proxiedPlayer.getServer().getInfo().getName());

                    if (serverInfo == null) {
                        proxiedPlayer.sendMessage("§cNão foi possível encontrar o servidor.");
                        return;
                    }


                    if (!lothPlayer.getSocial().getFriends().contains(target.getUniqueId())) {
                        proxiedPlayer.sendMessage("§cVocê não é amigo deste jogador.");
                        return;
                    }

                    if (!serverInfo.getType().name().contains("ROOM")) {
                        proxiedPlayer.sendMessage("§cO jogador §6" + args[1] + " §cnão está em uma partida.");
                        return;
                    }

                    if (proxiedPlayer.getServer().getInfo().getName().equalsIgnoreCase(target.getServer().getInfo().getName())) {
                        proxiedPlayer.sendMessage("§cVocê já está conectado neste servidor.");
                        return;
                    }

                    if (sI.getType().name().contains("ROOM")) {
                        proxiedPlayer.sendMessage("§cVocê não pode fazer isso em uma partida.");
                        return;
                    }

                    proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(serverInfo.getName()));

                    LothPlayer t = Core.getPlayerController().get(target.getUniqueId());

                    target.sendMessage("§d[AMIGO] " + lothPlayer.getGroup().getTag().getColor() + lothPlayer.getName() + "§e está te assistindo.");
                    proxiedPlayer.sendMessage("§d[AMIGO] §eVocê está assistindo " + t.getGroup().getTag().getColor() + t.getName() + "§e.");
                    return;
                }
                proxiedPlayer.sendMessage("§cSintaxe incorreta, utilize '/amigo assistir [amigo]'.");
                return;
            }

            ProxiedPlayer t = BungeeCore.getInstance().getProxy().getPlayer(s);

            if (t != null) {
                if (t == proxiedPlayer) {
                    proxiedPlayer.sendMessage("§cVocê não pode fazer isso consigo mesmo.");
                    return;
                }
            }

            LothPlayer target = (t != null ? Core.getPlayerController().get(t.getUniqueId()) : Core.getDataPlayer().get(s));

            if (target == null) {
                proxiedPlayer.sendMessage("§cUsuário não encontrado.");
                return;
            }

            if (lothPlayer.getSocial().hasFriend(target.getUniqueId())) {
                proxiedPlayer.sendMessage("§cVocê já é amigo deste jogador.");
                return;
            }

            if (target.getSocial().hasRequest(proxiedPlayer.getUniqueId())) {
                proxiedPlayer.sendMessage("§cVocê já enviou uma solicitação de amizade para este jogador.");
                return;
            }

            target.getSocial().addRequest(lothPlayer.getUniqueId());

            if (t!=null){
                TextComponent accept = new TextComponent("§eClique ");
                TextComponent here = new TextComponent("§a§lAQUI");
                TextComponent toAccept = new TextComponent("§e para aceitar ou clique ");
                TextComponent here2 = new TextComponent("§c§lAQUI");
                TextComponent toDeny = new TextComponent("§e para recusar.");

                here.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigo aceitar " + proxiedPlayer.getName()));
                here.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Clique para aceitar.").create()));

                here2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/amigo recusar " + proxiedPlayer.getName()));
                here2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Clique para recusar.").create()));

                accept.addExtra(here);
                accept.addExtra(toAccept);
                accept.addExtra(here2);
                accept.addExtra(toDeny);

                t.sendMessage("");
                t.sendMessage("§eVocê recebeu uma solicitação de amizade de §f" + lothPlayer.getGroup().getTag().getColor() + lothPlayer.getName() + "§e.");
                t.sendMessage(accept);
                t.sendMessage("");
            }
            proxiedPlayer.sendMessage("§aSolicitação enviada com sucesso.");
            Core.getDataPlayer().update(target);
            Core.getDataPlayer().update(lothPlayer);
            return;
        }
        return;
    }
}
