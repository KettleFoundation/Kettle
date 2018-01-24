package thermos.entity

import java.util.UUID

import org.bukkit.block.Block
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.projectiles.*

import com.mojang.authlib.GameProfile

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.server.management.ItemInWorldManager
import net.minecraft.util.ChunkCoordinates
import net.minecraft.util.IChatComponent
import net.minecraft.world.WorldServer
import net.minecraftforge.cauldron.entity.CraftCustomEntity

class CustomProjectileEntity(server: CraftServer, entity: Entity) : CraftCustomEntity(server, entity), Projectile {
    private var shooter: ProjectileSource? = null
    private var doesBounce: Boolean = false
    fun _INVALID_getShooter(): LivingEntity? {
        if (shooter is LivingEntity) {
            return shooter as LivingEntity?
        }
        if (shooter is BlockProjectileSource) {
            val block = (shooter as BlockProjectileSource).block
            if (block.world !is WorldServer) return null
            val x = block.x
            val y = block.y
            val z = block.z
            val ws = block.world as WorldServer
            val fake_dropper = EntityPlayerMP(MinecraftServer.getServer(), ws, dropper, ItemInWorldManager(MinecraftServer.getServer().worldServerForDimension(0)))
            fake_dropper.posX = x.toDouble()
            fake_dropper.posY = y.toDouble()
            fake_dropper.posZ = z.toDouble()
            val ce = org.bukkit.craftbukkit.entity.CraftEntity.getEntity(MinecraftServer.getServer().server, fake_dropper)
            return if (ce is LivingEntity) ce else null
        }
        return null
    }

    override fun getShooter(): ProjectileSource? {
        return shooter
    }

    fun _INVALID_setShooter(living: LivingEntity) {
        if (living is ProjectileSource) {
            this.shooter = living
        }
    }

    override fun setShooter(shooter: ProjectileSource) {
        this.shooter = shooter
    }

    override fun doesBounce(): Boolean {
        return doesBounce
    }

    override fun setBounce(doesBounce: Boolean) {
        this.doesBounce = doesBounce
    }

    override fun toString(): String {
        return "CraftCustomProjectile"
    }

    companion object {
        val dropper = GameProfile(UUID.nameUUIDFromBytes("[Dropper]".toByteArray()), "[Dropper]")
    }
}

