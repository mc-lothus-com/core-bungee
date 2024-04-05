package com.lothus.bungee.util.title;

import net.md_5.bungee.BungeeTitle;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TitleUtil {

    public static void sendTitle(ProxiedPlayer player, String title, String subtitle) {
        BungeeTitle packet = new BungeeTitle();
        packet.title(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', title)));
        packet.subTitle(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', subtitle)));
        packet.fadeIn(20);
        packet.fadeOut(10);
        packet.stay(10);
        packet.send(player);
    }
}
