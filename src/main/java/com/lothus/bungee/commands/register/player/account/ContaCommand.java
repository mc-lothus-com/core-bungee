package com.lothus.bungee.commands.register.player.account;

import com.lothus.bungee.BungeeCore;
import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.player.network.Network;
import com.lothus.core.punish.PunishesInfo;
import com.lothus.core.utils.checker.IpChecker;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;

public class ContaCommand extends BungeeCommandBase {
    public ContaCommand() {
        super("conta", "acc", "account", "info");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length == 0) {
                LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
                sender.sendMessage("");
                sender.sendMessage("§eUsuário: §f" + lothPlayer.getName());
                sender.sendMessage("§eVinculado: " + (lothPlayer.getSocial().getDiscord() != -1L ? "§aSim" : "§cNão"));
                sender.sendMessage("§eTipo de Conta: §f" + (lothPlayer.isPremium() ? "Premium" : "Pirata"));
                sender.sendMessage("§eRank: §f" + lothPlayer.getGroup().getRank().getName());
                sender.sendMessage(" §7§l➥ §eAdicionado em: §7" + new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(lothPlayer.getGroup().getCreated()));
                sender.sendMessage(" §7§l➥ §eAtualizado em: §7" + new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(lothPlayer.getGroup().getLastModified()));
                if (lothPlayer.getGroup().getRank().ordinal() <= Rank.GER.ordinal()) {
                    if (lothPlayer.getPunishStats() != null) {
                        sender.sendMessage("§eTotal de punições: §7" + lothPlayer.getPunishStats().getTotalPunishments());
                        sender.sendMessage(" §7§l➥ §eBanimentos: §7" + lothPlayer.getPunishStats().getBanPunishments());
                        sender.sendMessage(" §7§l➥ §eSilenciamentos: §7" + lothPlayer.getPunishStats().getMutePunishments());
                        sender.sendMessage(" §7§l➥ §ePunições revogadas: §7" + lothPlayer.getPunishStats().getPenaltiesRevoked());
                    }
                }
                sender.sendMessage("§ePrimeiro login: §f" + new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(lothPlayer.getFirstLogin()));
                sender.sendMessage("§eÚltimo login: §f" + new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(lothPlayer.getLastLogin()));
                if (!lothPlayer.getPunishes().isEmpty()) {
                    sender.sendMessage("§eHistórico de Punições:");
                    for (PunishesInfo punish : lothPlayer.getPunishes().values()) {
                        if (punish.getReason() == null)continue;
                        sender.sendMessage("§e" + punish.getId() + " §7- §e" + punish.getReason().getDisplay() + " §7- §e" + (punish.getExpires() == -1L ? "Nunca" : new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(punish.getExpires())));
                    }
                }


                IpChecker ipChecker = new IpChecker(((ProxiedPlayer) sender).getAddress().getHostString());
                if (lothPlayer.getNetwork() == null) {
                    lothPlayer.setNetwork(new Network());
                }

                if (ipChecker.getIpv4() == null) {
                    ipChecker.setIpv4(((ProxiedPlayer)sender).getAddress().getHostString());
                }

                lothPlayer.getNetwork().setIpv4(ipChecker.getIpv4());
                lothPlayer.getNetwork().setCity(ipChecker.getCity());
                lothPlayer.getNetwork().setIsp(ipChecker.getIsp());
                lothPlayer.getNetwork().setCountry(ipChecker.getCountry());
                lothPlayer.getNetwork().setStates(ipChecker.getStates());
                lothPlayer.setLastLogin(System.currentTimeMillis());
                sender.sendMessage("§eLocalização:");
                if (lothPlayer.getGroup().getRank().ordinal() <= Rank.GER.ordinal()) {
                    sender.sendMessage(" §7§l➥ §eIP: §7" + lothPlayer.getNetwork().getIpv4());
                } else {
                    sender.sendMessage(" §7§l➥ §eIP: §7" + lothPlayer.getNetwork().getIpv4().substring(0, 7) + "xxx");
                }
                sender.sendMessage(" §7País: " + lothPlayer.getNetwork().getCountry());
                sender.sendMessage(" §7Estado: " + lothPlayer.getNetwork().getStates());
                sender.sendMessage(" §7Cidade: " + lothPlayer.getNetwork().getCity());
                sender.sendMessage(" §7Provedor: §e" + lothPlayer.getNetwork().getIsp());
                sender.sendMessage("");
                return;
            }

            if (args.length > 0) {
                LothPlayer p = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());
                if (!(p.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal())) {
                    if (!p.getGroup().containsPermission("command.account")) {
                        sender.sendMessage(NO_PERMISSION);
                        return;
                    }
                }

                LothPlayer lothPlayer = Core.getDataPlayer().get(args[0]);

                if (lothPlayer == null) {
                    sender.sendMessage("§cJogador não encontrado.");
                    return;
                }

                sender.sendMessage("");
                sender.sendMessage("§eUsuário: §f" + lothPlayer.getName());
                sender.sendMessage("§eTipo de Conta: §f" + (lothPlayer.isPremium() ? "Premium" : "Pirata"));
                sender.sendMessage("§eRank: §f" + lothPlayer.getGroup().getRank().getName());
                sender.sendMessage(" §7§l➥ §eAdicionado em: §7" + new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(lothPlayer.getGroup().getCreated()));
                sender.sendMessage(" §7§l➥ §eAtualizado em: §7" + new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(lothPlayer.getGroup().getLastModified()));
                if (p.getGroup().getRank().ordinal() <= Rank.GER.ordinal()) {
                    if (lothPlayer.getPunishStats() != null) {
                        sender.sendMessage("§eTotal de punições: §7" + lothPlayer.getPunishStats().getTotalPunishments());
                        sender.sendMessage(" §7§l➥ §eBanimentos: §7" + lothPlayer.getPunishStats().getBanPunishments());
                        sender.sendMessage(" §7§l➥ §eSilenciamentos: §7" + lothPlayer.getPunishStats().getMutePunishments());
                        sender.sendMessage(" §7§l➥ §ePunições revogadas: §7" + lothPlayer.getPunishStats().getPenaltiesRevoked());
                    }
                }
                sender.sendMessage("§ePrimeiro login: §f" + new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(lothPlayer.getFirstLogin()));
                sender.sendMessage("§eÚltimo login: §f" + new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(lothPlayer.getLastLogin()));
                if (!lothPlayer.getPunishes().isEmpty()) {
                    sender.sendMessage("§eHistórico de Punições:");
                    for (PunishesInfo punish : lothPlayer.getPunishes().values()) {
                        if (punish.getReason() == null)continue;
                        sender.sendMessage("§e" + punish.getId() + " §7- §e" + punish.getReason().getDisplay() + " §7- §e" + (punish.getExpires() == -1L ? "Nunca" : new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(punish.getExpires())));
                    }
                }

                if (BungeeCore.getInstance().getProxy().getPlayer(args[0]) != null) {
                    ProxiedPlayer proxiedPlayer = BungeeCore.getInstance().getProxy().getPlayer(args[0]);
                    IpChecker ipChecker = new IpChecker(proxiedPlayer.getAddress().getHostString());

                    if (lothPlayer.getNetwork() == null) {
                        lothPlayer.setNetwork(new Network());
                    }

                    if (ipChecker.getIpv4() == null) {
                        ipChecker.setIpv4(proxiedPlayer.getAddress().getHostString());
                    }

                    lothPlayer.getNetwork().setIpv4(ipChecker.getIpv4());
                    lothPlayer.getNetwork().setCity(ipChecker.getCity());
                    lothPlayer.getNetwork().setIsp(ipChecker.getIsp());
                    lothPlayer.getNetwork().setCountry(ipChecker.getCountry());
                    lothPlayer.getNetwork().setStates(ipChecker.getStates());
                    sender.sendMessage("§eLocalização:");
                    if (p.getGroup().getRank().ordinal() <= Rank.GER.ordinal()) {
                        sender.sendMessage(" §7IP: §7" + lothPlayer.getNetwork().getIpv4());
                    } else {
                        sender.sendMessage(" §7IP: §7" + lothPlayer.getNetwork().getIpv4().substring(0, 7) + "xxx");
                    }
                    sender.sendMessage(" §7País: " + lothPlayer.getNetwork().getCountry());
                    sender.sendMessage(" §7Estado: " + lothPlayer.getNetwork().getStates());
                    sender.sendMessage(" §7Cidade: " + lothPlayer.getNetwork().getCity());
                    sender.sendMessage(" §7Provedor: §e" + lothPlayer.getNetwork().getIsp());
                    sender.sendMessage("");
                } else {
                    sender.sendMessage("");
                }
                return;
            }
        }
    }
}
