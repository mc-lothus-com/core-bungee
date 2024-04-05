package com.lothus.bungee.commands.register.moderation.report.menu;

import com.lothus.core.Core;
import com.lothus.core.controller.punish.ReportController;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.punish.report.ReportInfo;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemFlag;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.Sound;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Map;

public class ReportMenu {

    private final ProxiedPlayer player;
    private final ReportController reportController;

    public ReportMenu(ProxiedPlayer player) {
        this.player = player;
        this.reportController = Core.getReportController();
    }

    public void open(int page) {
        Inventory inventory = new Inventory(InventoryType.GENERIC_9X6);
        inventory.title("Reports - #" + page);

        List<ReportInfo> reports = reportController.getReports();
        ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(player.getUniqueId());

        inventory.item(48, new ItemStack(ItemType.GRAY_DYE).displayName((page == 1 ? "§cNão existe página anterior" : "§ePágina anterior: §7#" + (page - 1))));
        inventory.item(49, new ItemStack(ItemType.PAPER).displayName("§aReportes: §b" + reports.size()));
        inventory.item(50, new ItemStack(ItemType.LIME_DYE).displayName("§ePágina seguinte: §7#" + (page + 1)));

        if (reports.size() > 0) {
            for (int i = ((page * 28) - 28); i < Math.min((page * 28), reports.size()); i++) {
                ReportInfo reportInfo = reports.get(i);
                LothPlayer reported = Core.getDataPlayer().get(reportInfo.getReported());

                if (reported == null) continue;

                long count = reports.stream().filter(ri -> ri.getReported().equals(reportInfo.getReported())).count();

                if (inventory.items().entrySet().stream().noneMatch(entry -> ((String) entry.getValue().displayName(true)).replace("§e", "").equalsIgnoreCase("§f" + reported.getName()))) {
                    ItemStack itemStack = new ItemStack(ItemType.PLAYER_HEAD, (int) count).displayName("§e" + reported.getName());

                    itemStack.flagSet(ItemFlag.HIDE_ATTRIBUTES);
                    itemStack.addToLore("§1");
                    itemStack.addToLore("§fAutor: §7" + Core.getDataPlayer().get(reportInfo.getReporter()).getName());
                    itemStack.addToLore("§fMotivo: §7" + reportInfo.getReason());
                    itemStack.addToLore("§2");
                    itemStack.addToLore("§aClique esquerdo para teleportar ao jogador.");
                    itemStack.addToLore("§cClique direito para apagar reporte.");

                    inventory.item(slot(inventory), itemStack);
                }
            }
        }
        inventory.onClick(inventoryClick -> {
            inventoryClick.cancelled(true);

            if (inventoryClick.clickedItem() == null || inventoryClick.clickedItem().itemType() == null) return;

            if (inventoryClick.clickedItem().itemType() == ItemType.GRAY_DYE) {
                if (page > 1) {
                    protocolizePlayer.closeInventory();
                    open((page - 1));
                } else {
                    protocolizePlayer.playSound(Sound.ENTITY_VILLAGER_NO, SoundCategory.AMBIENT, 1, 1);
                }
                return;
            }
            if (inventoryClick.clickedItem().itemType() == ItemType.PAPER) {
                protocolizePlayer.playSound(Sound.ENTITY_VILLAGER_TRADE, SoundCategory.AMBIENT, 1, 1);
                return;
            }
            if (inventoryClick.clickedItem().itemType() == ItemType.LIME_DYE) {
                protocolizePlayer.closeInventory();
                open((page + 1));
                return;
            }
            LothPlayer lothPlayer = Core.getDataPlayer().get(((String) inventoryClick.clickedItem().displayName(true)).replace("§e", "").replace("§f", ""));
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(lothPlayer.getUniqueId());
            ReportInfo reportInfo = Core.getDataReport().get(lothPlayer.getUniqueId());

            inventoryClick.cancelled(true);

            if (inventoryClick.clickType() == ClickType.RIGHT_CLICK) {
                reportController.unload(reportInfo.getReported());
                Core.getDataReport().delete(reportInfo);
            }

            protocolizePlayer.closeInventory();

            if (inventoryClick.clickType() == ClickType.LEFT_CLICK) {
                if (proxiedPlayer != null && proxiedPlayer.isConnected()) {
                    if (proxiedPlayer.getServer().getInfo().getName() == player.getServer().getInfo().getName()) {
                        player.chat("/tp " + proxiedPlayer.getName());
                    } else {
                        player.chat("/go " + proxiedPlayer.getName());
                    }
                } else {
                    player.sendMessage(TextComponent.fromLegacyText("§cJogador não encontrado."));
                }
            }
        });

        protocolizePlayer.openInventory(inventory);
    }

    private final int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

    private int slot(Inventory inventory) {
        for (int slot : slots) {
            Map<Integer, BaseItemStack> items = inventory.items();

            if (items.get(slot) == null || items.get(slot).itemType() == ItemType.AIR) {
                return slot;
            }
        }
        return 10;
    }

}
