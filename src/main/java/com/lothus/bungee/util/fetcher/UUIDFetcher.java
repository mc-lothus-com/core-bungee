package com.lothus.bungee.util.fetcher;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lothus.core.Core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDFetcher {

    private final List<String> apis;

    public UUIDFetcher() {
        this.apis = new ArrayList<>();
    }

    public void init() {
        this.apis.add("https://api.mojang.com/users/profiles/minecraft/%s");
        this.apis.add("https://api.minetools.eu/uuid/%s");
        this.apis.add("https://minecraft-api.com/api/uuid/uuid.php?pseudo=%s");
    }

    private UUID request(final String name) throws Exception {
        UUID uuid = null;

        for (String api : apis) {
            try {
                uuid = request(api, name);
                break;
            } catch (Exception ignored) {
            }
        }
        if (uuid == null) {
            throw new Exception("Unable to verify session.");
        }
        return uuid;
    }

    private UUID request(String api, final String name) throws Exception {
        final URLConnection con = new URL(String.format(api, name)).openConnection();
        final JsonElement element = Core.getParser().parse(new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)));

        if (element instanceof JsonObject) {
            final JsonObject object = (JsonObject) element;
            if (object.has("error") || object.has("errorMessage")) {
                throw new Exception(object.get("errorMessage").getAsString());
            }
            if (object.has("id")) {
                return UUIDParser.parse(object.get("id"));
            }
            if (object.has("uuid")) {
                final JsonObject uuid = object.getAsJsonObject("uuid");
                if (uuid.has("formatted")) {
                    return UUIDParser.parse(object.get("formatted"));
                }
            }
        }
        System.out.println(element);
        return null;
    }

    public UUID getUUID(final String name) throws Exception {
        if (name.matches("[a-zA-Z0-9_]{3,16}")) {
            return request(name);
        }
        return UUIDParser.parse(name);
    }
}
