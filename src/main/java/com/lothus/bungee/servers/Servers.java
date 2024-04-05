package com.lothus.bungee.servers;

import com.lothus.core.Core;
import com.lothus.core.servers.ServerInfo;
import net.md_5.bungee.api.ProxyServer;

import java.net.InetSocketAddress;

public class Servers {

    public void update(ServerInfo server) {
        net.md_5.bungee.api.config.ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(server.getName(), fromAddress(server), "", false);
        if (ProxyServer.getInstance().getServers().containsKey(server.getName())) {
            ProxyServer.getInstance().getServers().replace(server.getName(), serverInfo);
        }
        ProxyServer.getInstance().getServers().put(server.getName(), serverInfo);
        Core.getServerController().unload(server.getName());
        Core.getServerController().load(server);
    }

    public InetSocketAddress fromAddress(ServerInfo serverInfo) {
        InetSocketAddress address = new InetSocketAddress(serverInfo.getAddress(), serverInfo.getPort());
        return address;
    }

    public void unloadServers() {
        ProxyServer.getInstance().getServers().clear();
    }

    public void unloadServer(ServerInfo serverInfo) {
        if (ProxyServer.getInstance().getServers().containsKey(serverInfo.getName())) {
            ProxyServer.getInstance().getServers().remove(serverInfo.getName());
        }
        Core.getServerController().unload(serverInfo.getName());
    }

    public void loadServer(ServerInfo server) {
        net.md_5.bungee.api.config.ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(server.getName(), fromAddress(server), "MOtd", false);
        ProxyServer.getInstance().getServers().put(server.getName(), serverInfo);
        Core.getServerController().load(server);
    }
}
