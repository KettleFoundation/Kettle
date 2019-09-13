package org.kettlemc.api;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAPI {

    public static Map<String, Integer> mods = new ConcurrentHashMap<>();
    public static Set<String> modList = new ConcurrentSet<>();

    public static int getModSize() {
        return mods.get("mods") == null ? 0 : mods.get("mods");
    }

    public static String getModList() {
        return modList.toString();
    }

    public static boolean hasMod(String modid) {
        return getModList().contains(modid);
    }

    public static MinecraftServer getNMSServer() {
        return MinecraftServer.getServerInstance();
    }

    public static CraftServer getCBServer() {
        return (CraftServer) Bukkit.getServer();
    }
}
