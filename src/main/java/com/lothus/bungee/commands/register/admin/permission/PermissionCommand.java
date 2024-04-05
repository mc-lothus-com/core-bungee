package com.lothus.bungee.commands.register.admin.permission;


import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class PermissionCommand extends BungeeCommandBase {

    public PermissionCommand() {
        super("addpermission");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            LothPlayer lothPlayer = Core.getPlayerController().get(((ProxiedPlayer) sender).getUniqueId());

            if (lothPlayer.getGroup().getRank() != Rank.CEO) {
                if (!lothPlayer.getGroup().containsPermission("command.addpermission")) {
                    sender.sendMessage("§cVocê não tem permissão para executar este comando!");
                    return;
                }
            }


            if (args.length == 0) {
                sender.sendMessage("§cSintaxe incorreta, utilize '/addpermission [jogador] [permissão] [tempo]'.");
                return;
            }

            if (args.length > 0) {
                LothPlayer target = Core.getDataPlayer().get(args[0]);

                if (target == null) {
                    sender.sendMessage("§cO jogador não foi encontrado.");
                    return;
                }

                if (args.length > 1) {
                    String permission = args[1];

                    if (args.length > 2) {
                        if (args[2].equalsIgnoreCase("permanent")) {
                            target.getGroup().addPermission(permission, -1L);
                        } else if (args[2].endsWith("d")) {
                            target.getGroup().addPermission(permission, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(Long.parseLong(args[2].split("d")[0])));
                        } else if (args[2].endsWith("s")) {
                            target.getGroup().addPermission(permission, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Long.parseLong(args[2].split("s")[0])));
                        } else if (args[2].endsWith("m")) {
                            target.getGroup().addPermission(permission, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(Long.parseLong(args[2].split("m")[0])));
                        } else if (args[2].endsWith("h")) {
                            target.getGroup().addPermission(permission, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(Long.parseLong(args[2].split("h")[0])));
                        } else {
                            sender.sendMessage("§cSintaxe incorreta, utilize '/addpermission [jogador] [permissão] [1s/1m/1h/1d]'.");
                            return;
                        }

                        sender.sendMessage("§aPermissão adicionada com sucesso!");
                        Core.getDataPlayer().update(target);
                    }
                } else {
                    sender.sendMessage("§cSintaxe incorreta, utilize '/addpermission [jogador] [permissão] [tempo]'.");
                    return;
                }
            }
        } else {
            if (args.length == 0) {
                sender.sendMessage("§cSintaxe incorreta, utilize '/addpermission [jogador] [permissão] [tempo]'.");
                return;
            }

            if (args.length > 0) {
                LothPlayer target = Core.getDataPlayer().get(args[0]);

                if (target == null) {
                    sender.sendMessage("§cO jogador não foi encontrado.");
                    return;
                }

                if (args.length > 1) {
                    String permission = args[1];

                    if (args.length > 2) {
                        if (args[2].equalsIgnoreCase("permanent")) {
                            target.getGroup().addPermission(permission, -1L);
                        } else if (args[2].endsWith("d")) {
                            target.getGroup().addPermission(permission, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(Long.parseLong(args[2].split("d")[0])));
                        } else if (args[2].endsWith("s")) {
                            target.getGroup().addPermission(permission, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Long.parseLong(args[2].split("s")[0])));
                        } else if (args[2].endsWith("m")) {
                            target.getGroup().addPermission(permission, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(Long.parseLong(args[2].split("m")[0])));
                        } else if (args[2].endsWith("h")) {
                            target.getGroup().addPermission(permission, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(Long.parseLong(args[2].split("h")[0])));
                        } else {
                            sender.sendMessage("§cSintaxe incorreta, utilize '/addpermission [jogador] [permissão] [1s/1m/1h/1d]'.");
                            return;
                        }

                        Core.getDataPlayer().update(target);
                        sender.sendMessage("§aPermissão adicionada com sucesso!");
                    }
                } else {
                    sender.sendMessage("§cSintaxe incorreta, utilize '/addpermission [jogador] [permissão] [tempo]'.");
                    return;
                }
            }
        }
    }
}
