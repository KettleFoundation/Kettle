package kettle.chaud

import net.minecraft.entity.player.EntityPlayer

object DesUtils {

    fun isModded(pkg: String): Boolean {
        return !(pkg.startsWith("net.minecraft") || pkg.startsWith("org.bukkit") || pkg.startsWith("net.minecraftforge.fml.common") || pkg.startsWith("org.spigotmc") || pkg.startsWith("kettle"))
    }
}
