package thermos.chaud

import net.minecraft.entity.player.EntityPlayer

object DesUtils {

    fun isModded(pkg: String): Boolean {
        return !(pkg.startsWith("net.minecraft") || pkg.startsWith("org.bukkit") || pkg.startsWith("cpw.mods.fml") || pkg.startsWith("org.spigotmc") || pkg.startsWith("thermos"))
    }
}
