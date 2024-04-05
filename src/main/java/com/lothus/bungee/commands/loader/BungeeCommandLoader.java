package com.lothus.bungee.commands.loader;

import com.lothus.bungee.commands.BungeeCommandBase;
import com.lothus.bungee.util.classes.ClassGetter;
import com.lothus.core.Core;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCommandLoader {

    @SuppressWarnings("deprecation")
    public static void loadCommands(Plugin instance, String packageName) {
        for (Class<?> commandClass : ClassGetter.getClassesForPackage(instance, packageName)) {
            if (BungeeCommandBase.class.isAssignableFrom(commandClass)) {
                try {
                    BungeeCommandBase commands = (BungeeCommandBase) commandClass.newInstance();
                    ProxyServer.getInstance().getPluginManager().registerCommand(instance, commands);
                } catch (Exception e) {
                    Core.getLogger()
                            .warning("> Não foi possível carregar o comando da class " + commandClass.getSimpleName() + ".");
                }
            }
        }
    }

}
