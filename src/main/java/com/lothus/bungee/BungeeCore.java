package com.lothus.bungee;

import com.lothus.bungee.api.settings.Settings;
import com.lothus.bungee.commands.loader.BungeeCommandLoader;
import com.lothus.bungee.listeners.chat.PlayerChatListener;
import com.lothus.bungee.listeners.party.PartyListener;
import com.lothus.bungee.listeners.ping.PingListener;
import com.lothus.bungee.listeners.player.PlayerListener;
import com.lothus.bungee.listeners.server.ServerListener;
import com.lothus.bungee.redis.BungeeListener;
import com.lothus.bungee.servers.Servers;
import com.lothus.core.Core;
import com.lothus.core.data.party.DataParty;
import com.lothus.core.data.player.DataPlayer;
import com.lothus.core.data.report.DataReport;
import com.lothus.core.data.server.DataServer;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import com.lothus.core.storage.redis.bungee.RedisBungee;
import com.lothus.core.utils.fetcher.UUIDFetcher;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static com.lothus.core.storage.redis.channels.RedisChannel.*;

@Getter @Setter
public class BungeeCore extends Plugin {

    @Getter @Setter
    private static BungeeCore instance;

    private Settings settings;
    private Servers servers;
    private Configuration configuration;
    private String motd;

    public void load() {
        setInstance(this);
        setSettings(new Settings());
        createConfig();
        setServers(new Servers());
        Core.setLogger(getLogger());
        Core.setRedis(new RedisBungee());
        Core.setUniqueIdFetcher(
                new UUIDFetcher()
        );
        Core.getRedis().start(
                getConfiguration().getString("redis.host"),
                getConfiguration().getInt("redis.port"),
                getConfiguration().getString("redis.password")
        );
        Core.getMongo().start(
                getConfiguration().getString("mongo.host"),
                getConfiguration().getInt("mongo.port")
        );
        Core.setDataParty(new DataParty());
        Core.setDataPlayer(new DataPlayer());
        Core.setDataServer(new DataServer());
        Core.setDataReport(new DataReport());
        Core.getUniqueIdFetcher().init();
    }

    public void enable() {
        createServerInfo();
        createMotd();
        Core.getReportController().loadAll();
        getProxy().getPluginManager().registerListener(this, new PingListener());
        getProxy().getPluginManager().registerListener(this, new PartyListener());
        getProxy().getPluginManager().registerListener(this, new ServerListener());
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
        getProxy().getPluginManager().registerListener(this, new PlayerChatListener());
        BungeeCommandLoader.loadCommands(this, "com.lothus.bungee.commands.register");

        runAsync();
    }

    public void disable() {
        Core.getMongo().stop();
        Core.getRedis().shutdown();
    }


    @Override
    public void onLoad() {
        load();
    }

    @Override
    public void onEnable() {
        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }


    private void runAsync() {
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        getProxy().getScheduler().runAsync(instance, new RedisBungee.PubSubTask(
                                new BungeeListener(),
                                SERVER_START.name(),
                                SERVER_STOP.name(),
                                SERVER_UPDATE.name(),
                                PLAYER_CONNECT_SERVER.name(),
                                PLAYER_ACCOUNT_UPDATE.name(),
                                GAME_START.name(),
                                GAME_UPDATE.name(),
                                GAME_STOP.name(),
                                DISCORD_VINCULE_ACCOUNT.name(),
                                APP_UPDATE_ACCOUNT.name(),
                                BUY_IN_APP.name(),
                                MAINTENANCE.name()));
                    }
                });
        thread.start();
    }

    private void createServerInfo() {
        ServerInfo serverInfo = Core.getDataServer().get(ServerType.PROXY);
        if (serverInfo == null) {
            serverInfo = new ServerInfo(
                    getConfiguration().getInt("server.id"),
                    getConfiguration().getString("server.name"),
                    ServerType.valueOf(getConfiguration().getString("server.type")),
                    getConfiguration().getInt("server.port")
            );
            Core.getDataServer().create(serverInfo);
        }

        Core.setServerInfo(serverInfo);

        serverInfo.setPlayers(0);
        serverInfo.setType(ServerType.valueOf(getConfiguration().getString("server.type")));
        serverInfo.getConfiguration().setMaxPlayers(getConfiguration().getInt("server.maxPlayers"));

        Core.getDataServer().update(serverInfo);
        Core.getServerController().load(serverInfo);
    }
    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(this.getDataFolder(), "config.yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createMotd() {
        motd = configuration.getString("server.motd-bungee").replace("&", "§").replace("{newline}", "\n");
    }
}
