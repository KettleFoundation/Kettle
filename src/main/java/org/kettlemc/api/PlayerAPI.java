package org.kettlemc.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerAPI {
    public static Map<EntityPlayerMP, Integer> mods = new ConcurrentHashMap<>();
    public static Map<EntityPlayerMP, String> modList = new ConcurrentHashMap<>();

    public static EntityPlayerMP getNMSPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public static Player getCBPlayer(EntityPlayerMP playerMP) {
        return playerMP.getBukkitEntity().getPlayer();
    }

    public static boolean isOp(EntityPlayer entityPlayer) {
        return ServerAPI.getNMSServer().getPlayerList().canSendCommands(entityPlayer.getGameProfile());
    }

    public static int getModAmount(Player player) {
        return mods.get(getNMSPlayer(player)) == null ? 0 : mods.get(getNMSPlayer(player));
    }

    public static String getModlist(Player player) {
        return modList.get(getNMSPlayer(player)) == null ? "null" : modList.get(getNMSPlayer(player));
    }

    public static boolean hasMod(Player player, String modid) {
        return getModlist(player).contains(modid);
    }
}
