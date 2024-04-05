package com.lothus.bungee.commands.register.player.party;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.player.party.Party;
import com.lothus.core.player.party.state.PartyState;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

public class PartyCommand extends BungeeCommandBase {

    private HashMap<UUID, Long> cooldown = new HashMap<>();

    public PartyCommand() {
        super(
                "party",
                "party"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (args.length == 0) {
            player.sendMessage("");
            player.sendMessage("§eAjuda - Party");
            player.sendMessage("");
            player.sendMessage("§3/party [jogador] §8- §3Enviar convite de party.");
            player.sendMessage("§3/party entrar [jogador] §8- §3Entrar em uma party pública.");
            player.sendMessage("§3/party remover [jogador] §8- §3Remover um jogador da party.");
            player.sendMessage("§3/party aceitar [jogador] §8- §3Aceitar um convite para party.");
            player.sendMessage("§3/party recusar [jogador] §8- §3Recusar um convite para party.");
            player.sendMessage("§3/party transferir [jogador] §8- §3Transferir a party para outro jogador.");
            player.sendMessage("§3/party abrir [tamanho] §8- §3Abrir a party com um tamanho personalizado.");
            player.sendMessage("§3/party teleportar §8- §3Teleporte os jogadores para seu servidor.");
            player.sendMessage("§3/party chat §8- §3Ativar/desativar o chat da party.");
            player.sendMessage("§3/party fechar §8- §3Fechar/desfazer a party.");
            player.sendMessage("");
            return;
        }

        if (args.length > 0) {
            String s = args[0];
            if (s.equalsIgnoreCase("aceitar") || s.equalsIgnoreCase("accept")) {
                if (args.length > 1) {
                    if (Core.getPartyController().get(player.getUniqueId()) != null) {
                        player.sendMessage("§cVocê já está em uma party.");
                        return;
                    }

                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                    if (target == null) {
                        player.sendMessage("§cUsuário não encontrado.");
                        return;
                    }

                    Party party = Core.getPartyController().get(target.getUniqueId());

                    if (party == null) {
                        player.sendMessage("§cO jogador §6" + target.getName() + "§c não está em uma party.");
                        return;
                    }

                    if ((!party.getRequests().containsKey(player.getUniqueId())) || party.getRequests().get(player.getUniqueId()) < System.currentTimeMillis()) {
                        player.sendMessage("§cVocê não foi convidado por este jogador ou ele expirou.");
                        party.removeRequest(player.getUniqueId());
                        return;
                    }

                    if (party.getLeader() != target.getUniqueId()) {
                        player.sendMessage("§cEste jogador não é mais o lider da party.");
                        return;
                    }

                    if ((party.getMembers().size() + 1) >= party.getSlots()) {
                        player.sendMessage("§cA party desde jogador está cheia.");
                        return;
                    }

                    party.add(player.getUniqueId());
                    party.removeRequest(player.getUniqueId());

                    for (UUID uuid : party.getMembers()) {
                        ProxiedPlayer find = ProxyServer.getInstance().getPlayer(uuid);

                        if (find == null) {
                            party.remove(uuid);
                            party.removeRequest(uuid);
                            continue;
                        }

                        find.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e entrou na party.");
                    }
                    target.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e entrou na party.");
                    Core.getDataParty().update(party);
                    return;
                }

                player.sendMessage("§cSintaxe incorreta, utilize '/party aceitar [jogador]' para continuar.");
                return;
            } else if (s.equalsIgnoreCase("recusar") || s.equalsIgnoreCase("deny")) {
                if (args.length > 1) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                    if (target == null) {
                        player.sendMessage("§cUsuário não encontrado.");
                        return;
                    }

                    Party party = Core.getPartyController().get(target.getUniqueId());

                    if (party == null) {
                        player.sendMessage("§cO jogador §6" + target.getName() + "§c não está em uma party.");
                        return;
                    }

                    if ((!party.getRequests().containsKey(player.getUniqueId())) || party.getRequests().get(player.getUniqueId()) < System.currentTimeMillis()) {
                        player.sendMessage("§cVocê não foi convidado por este jogador ou ele expirou.");
                        return;
                    }

                    if (party.getLeader() != target.getUniqueId()) {
                        player.sendMessage("§cEste jogador não é mais o lider da party.");
                        return;
                    }

                    party.removeRequest(target.getUniqueId());
                    target.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + " §crecusou seu convite.");
                    Core.getDataParty().update(party);
                    return;
                }

                player.sendMessage("§cSintaxe incorreta, utilize '/party recusar [jogador]' para continuar.");
                return;
            } else if (s.equalsIgnoreCase("remover")) {
                if (args.length > 1) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage("§cUsuário não encontrado.");
                        return;
                    }

                    Party party = Core.getPartyController().get(player.getUniqueId());

                    if (party == null) {
                        player.sendMessage("§cVocê não está em uma party.");
                        return;
                    }

                    if (party.getLeader() != player.getUniqueId()) {
                        player.sendMessage("§cVocê não é o líder da party.");
                        return;
                    }

                    if (!party.hasMember(target.getUniqueId())) {
                        player.sendMessage("§cEste jogador não faz parte da sua party.");
                        return;
                    }

                    party.remove(target.getUniqueId());
                    party.removeRequest(target.getUniqueId());

                    target.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + " §cremoveu você da party.");
                    player.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + target.getName() + " §cfoi removido da party.");
                    Core.getDataParty().update(party);
                    return;
                }
            } else if (s.equalsIgnoreCase("transferir") || s.equalsIgnoreCase("transfer")) {
                if (args.length > 1) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                    if (target == null) {
                        player.sendMessage("§cUsuário não encontrado.");
                        return;
                    }

                    Party party = Core.getPartyController().get(player.getUniqueId());

                    if (party == null) {
                        player.sendMessage("§cVocê não está em uma party.");
                        return;
                    }

                    if (party.getLeader() != player.getUniqueId()) {
                        player.sendMessage("§cVocê não é o líder da party.");
                        return;
                    }

                    if (!party.hasMember(target.getUniqueId())) {
                        player.sendMessage("§cEste jogador não faz parte da sua party.");
                        return;
                    }

                    party.setLeader(target.getUniqueId());
                    party.add(player.getUniqueId());
                    party.remove(target.getUniqueId());

                    LothPlayer l = Core.getPlayerController().get(target.getUniqueId());
                    for (UUID uniqueId : party.getMembers()) {
                        ProxiedPlayer find = ProxyServer.getInstance().getPlayer(uniqueId);

                        if (find == null) {
                            party.remove(uniqueId);
                            party.removeRequest(uniqueId);
                            continue;
                        }

                        find.sendMessage("§d[PARTY] §eA posse da party foi transferida para " + l.getGroup().getTag().getColor() + target.getName() + "§e.");
                    }
                    target.sendMessage("§d[PARTY] §eA posse da party foi transferida para " + l.getGroup().getTag().getColor() + target.getName() + "§e.");
                    Core.getDataParty().update(party);
                    return;
                }
                player.sendMessage("§cSintaxe incorreta, utilize '/party transferir [jogador]' para continuar.");
                return;
            } else if (s.equalsIgnoreCase("abrir") || s.equalsIgnoreCase("open")) {
                Party party = Core.getPartyController().get(player.getUniqueId());

                if (party == null) {
                    player.sendMessage("§cVocê não faz parte de uma party.");
                    return;
                }

                if (!party.isLeader(player.getUniqueId())) {
                    player.sendMessage("§cVocê não é o líder da party.");
                    return;
                }

                if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.MIDIA.ordinal())) {
                    if (!lothPlayer.getGroup().containsPermission("party.open")) {
                        player.sendMessage(NO_PERMISSION);
                        return;
                    }
                }

                if (party.isState(PartyState.OPEN)) {
                    player.sendMessage("§cA sua party já está aberta para " + party.getSlots() + " pessoas.");
                    return;
                }

                if (args.length > 1) {
                    if (!isInteger(args[1])) {
                        player.sendMessage("§cO tamanho da party deve ser um número inteiro.");
                        return;
                    }

                    int size = Integer.parseInt(args[1]);

                    party.open(size);
                    player.sendMessage("§aA sua party agora está aberta para " + size + " pessoas.");
                    Core.getDataParty().update(party);
                    return;
                }
                player.sendMessage("§cSintaxe incorreta, utilize '/party abrir [tamanho]' para continuar.");
                return;
            } else if (s.equalsIgnoreCase("entrar") || s.equalsIgnoreCase("join")) {
                if (args.length > 1) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);

                    if (target == null) {
                        player.sendMessage("§cUsuário não encontrado.");
                        return;
                    }

                    Party party = Core.getPartyController().get(target.getUniqueId());

                    if (party == null) {
                        player.sendMessage("§cO jogador §6" + target.getName() + "§c não está em uma party.");
                        return;
                    }

                    if (!party.isLeader(target.getUniqueId())) {
                        player.sendMessage("§cEste jogador não é o líder da party.");
                        return;
                    }

                    if (!party.isState(PartyState.OPEN)) {
                        player.sendMessage("§cA party desde jogador não é pública.");
                        return;
                    }

                    if ((party.getMembers().size() + 1) >= party.getSlots()) {
                        player.sendMessage("§cA party desde jogador está cheia.");
                        return;
                    }

                    if (party.hasMember(player.getUniqueId())) {
                        player.sendMessage("§cVocê já está nesta party.");
                        return;
                    }

                    party.add(player.getUniqueId());

                    for (UUID uniqueId : party.getMembers()) {
                        ProxiedPlayer find = ProxyServer.getInstance().getPlayer(uniqueId);

                        if (find == null) {
                            party.remove(uniqueId);
                            party.removeRequest(uniqueId);
                            continue;
                        }

                        find.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e entrou na party.");
                    }
                    ProxiedPlayer leader = ProxyServer.getInstance().getPlayer(party.getLeader());
                    leader.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e entrou na party.");
                    Core.getDataParty().update(party);
                    return;
                }
                player.sendMessage("§cSintaxe incorreta, utilize '/party entrar [jogador]' para continuar.");
                return;
            } else if (s.equalsIgnoreCase("fechar") || s.equalsIgnoreCase("desfazer")) {
                Party party = Core.getPartyController().get(player.getUniqueId());

                if (party == null) {
                    player.sendMessage("§cVocê não está em uma party.");
                    return;
                }

                if (!party.isLeader(player.getUniqueId())) {
                    player.sendMessage("§cVocê não é o líder da party.");
                    return;
                }

                for (UUID uniqueId : party.getMembers()) {
                    ProxiedPlayer find = ProxyServer.getInstance().getPlayer(uniqueId);

                    if (find == null) {
                        party.remove(uniqueId);
                        party.removeRequest(uniqueId);
                        continue;
                    }

                    find.sendMessage("§d[PARTY] §eA party foi desfeita por " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e.");
                }

                Core.getPartyController().unload(party);
                player.sendMessage("§d[PARTY] §eA party foi desfeita por " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e.");
                Core.getDataParty().del(party);
                return;
            } else if (s.equalsIgnoreCase("sair")) {
                Party party = Core.getPartyController().get(player.getUniqueId());

                if (party == null) {
                    player.sendMessage("§cVocê não está em uma party.");
                    return;
                }

                if (party.isLeader(player.getUniqueId())) {
                    player.sendMessage("§cPara sair da party você deve desfazer a mesma.");
                    return;
                }

                party.remove(player.getUniqueId());
                party.removeRequest(player.getUniqueId());

                for (UUID uniqueId : party.getMembers()) {
                    ProxiedPlayer find = ProxyServer.getInstance().getPlayer(uniqueId);

                    if (find == null) {
                        party.remove(uniqueId);
                        party.removeRequest(uniqueId);
                        continue;
                    }

                    find.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e saiu da party.");
                }
                ProxiedPlayer leader = ProxyServer.getInstance().getPlayer(party.getLeader());
                leader.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e saiu da party.");
                player.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e saiu da party.");
                Core.getDataParty().update(party);
                return;
            } else if (s.equalsIgnoreCase("info")) {
                Party party = Core.getPartyController().get(player.getUniqueId());

                if (party == null) {
                    player.sendMessage("§cVocê não está em uma party.");
                    return;
                }

                ProxiedPlayer leader = ProxyServer.getInstance().getPlayer(party.getLeader());

                player.sendMessage("");
                player.sendMessage("§dLíder: §7" + leader.getName());
                player.sendMessage("§dLimite: §7" + party.getSlots());
                player.sendMessage("§dMax. Limite: §7" + party.getMaxSize());
                player.sendMessage("§dMembros:");

                TextComponent members = new TextComponent();
                for (UUID uniqueId : party.getMembers()) {
                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uniqueId);

                    if (target == null) {
                        party.remove(target.getUniqueId());
                        party.removeRequest(target.getUniqueId());
                        continue;
                    }

                    LothPlayer p = Core.getPlayerController().get(uniqueId);
                    TextComponent component = new TextComponent("  §7● " + p.getGroup().getRank().getColor() + target.getName() + "\n");
                    members.addExtra(component);
                }
                player.sendMessage(members);
                player.sendMessage("");
                return;
            } else if (s.equalsIgnoreCase("teleportar") || s.equalsIgnoreCase("warp")) {
                Party party = Core.getPartyController().get(player.getUniqueId());

                if (party == null) {
                    player.sendMessage("§cVocê não está em uma party.");
                    return;
                }

                if(!party.isLeader(player.getUniqueId())) {
                    player.sendMessage("§cVocê não é o líder da party.");
                    return;
                }

                for (UUID uniqueId : party.getMembers()) {
                    ProxiedPlayer find = ProxyServer.getInstance().getPlayer(uniqueId);

                    if (find == null) {
                        party.remove(uniqueId);
                        party.removeRequest(uniqueId);
                        continue;
                    }

                    ServerInfo serverInfo = Core.getServerController().get(find.getServer().getInfo().getName());

                    if (serverInfo.getType() == ServerType.LOGIN)continue;

                    find.connect(player.getServer().getInfo());
                    find.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e teleportou os jogadores.");
                }
                player.sendMessage("§d[PARTY] " + lothPlayer.getGroup().getTag().getColor() + player.getName() + "§e teleportou os jogadores.");
                return;
            } else if (s.equalsIgnoreCase("chat")) {
                Party party = Core.getPartyController().get(player.getUniqueId());

                if (party == null) {
                    player.sendMessage("§cVocê não está em uma party.");
                    return;
                }

                if (!party.isLeader(player.getUniqueId())) {
                    player.sendMessage("§cVocê não é o líder da party.");
                    return;
                }

                party.setChat(!party.isChat());
                player.sendMessage("§eVocê §b" + (party.isChat() ? "ativou" : "desativou") + " §eo chat da party.");
                return;
            }

            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage("§cUsuário não encontrado.");
                return;
            }

            if (player.getUniqueId() == target.getUniqueId()) {
                player.sendMessage("§cVocê não pode convidar a si mesmo.");
                return;
            }

            ServerInfo serverInfo = Core.getServerController().get(target.getServer().getInfo().getName());

            if (serverInfo == null)return;
            if (serverInfo.getType() == ServerType.LOGIN) {
                player.sendMessage("§cVocê não pode enviar party para este jogador.");
                return;
            }

            Party party = Core.getPartyController().get(player.getUniqueId());

            if (party == null) {
                party = new Party(player.getUniqueId());
                Core.getDataParty().create(party);
            } else {
                if (!party.isLeader(player.getUniqueId())) {
                    player.sendMessage("§cVocê não é o líder da party.");
                    return;
                }
            }

            Party targetParty = Core.getPartyController().get(target.getUniqueId());

            if (targetParty != null) {
                player.sendMessage("§cEste jogador já está em uma party.");
                return;
            }

            if (party.getMembers().size() >= party.getSlots()) {
                player.sendMessage("§cA sua party está cheia.");
                return;
            }

            if (party.getRequests().containsKey(target.getUniqueId())) {
                player.sendMessage("§cEste jogador já foi convidado.");
                return;
            }

            party.addRequest(target.getUniqueId());

            TextComponent accept = new TextComponent("§eClique ");
            TextComponent here = new TextComponent("§a§lAQUI");
            TextComponent toAccept = new TextComponent("§e para aceitar ou clique ");
            TextComponent here2 = new TextComponent("§c§lAQUI");
            TextComponent toDeny = new TextComponent("§e para recusar.");

            here.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party aceitar " + player.getName()));
            here.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Clique para aceitar.").create()));

            here2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party recusar " + player.getName()));
            here2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Clique para recusar.").create()));

            accept.addExtra(here);
            accept.addExtra(toAccept);
            accept.addExtra(here2);
            accept.addExtra(toDeny);

            target.sendMessage("");
            target.sendMessage("§eVocê recebeu um convite para a party de §f" + lothPlayer.getGroup().getTag().getColor() + lothPlayer.getName() + "§e.");
            target.sendMessage(accept);
            target.sendMessage("");

            LothPlayer targetPlayer = Core.getPlayerController().get(target.getUniqueId());
            for (UUID uniqueId : party.getMembers()) {
                ProxiedPlayer find = ProxyServer.getInstance().getPlayer(uniqueId);

                if (find == null) {
                    party.remove(uniqueId);
                    party.removeRequest(uniqueId);
                    continue;
                }

                find.sendMessage("§d[PARTY] " + targetPlayer.getGroup().getTag().getColor() + target.getName() + "§e foi convidado para a party.");
            }
            player.sendMessage("§d[PARTY] " + targetPlayer.getGroup().getTag().getColor() + target.getName() + "§e foi convidado para a party.");

            Core.getPartyController().load(party);
            return;
        }
    }
}
