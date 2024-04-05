package com.lothus.bungee.commands.register.moderation.report;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.punish.report.ReportInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReportCommand extends BungeeCommandBase {

    private final Cache<UUID, Boolean> delay = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();

    public ReportCommand() {
        super(
                "report",
                "denunciar",
                "reportar"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

        if (delay.getIfPresent(proxiedPlayer.getUniqueId()) != null) {
            proxiedPlayer.sendMessage("§cAguarde para denunciar novamente...");
            return;
        }

        if (args.length == 0) {
            proxiedPlayer.sendMessage("§cSintaxe incorreta, utilize '/report [usuário] [motivo]'.");
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target == null) {
            proxiedPlayer.sendMessage("§cUsuário não encontrado.");
            return;
        }

        LothPlayer targetPlayer = Core.getPlayerController().get(target.getUniqueId());
        String reason = "Sem motivo";

        if (args.length > 1) {
            reason = getArgs(args, 1);
        }
        ReportInfo reportInfo = new ReportInfo(
                proxiedPlayer.getUniqueId(),
                targetPlayer.getUniqueId(),
                reason,
                System.currentTimeMillis()
        );

        Core.getReportController().load(reportInfo);
        Core.getDataReport().create(reportInfo);

        proxiedPlayer.sendMessage("§aReporte enviado com sucesso.");
        delay.put(proxiedPlayer.getUniqueId(), true);

        TextComponent message = new TextComponent("§2§lREPORT: §fUma nova denúncia foi encontrada, atualmente existem §2" + Core.getReportController().getReports().size() + " §adenúncias.");

        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eClique para ver.")));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports"));

        for (ProxiedPlayer o : ProxyServer.getInstance().getPlayers()) {
            LothPlayer t = Core.getPlayerController().get(o.getUniqueId());
            if (t.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal()) {
                o.sendMessage(message);
            }
        }
    }
}
