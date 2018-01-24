package thermos.thermite

import net.minecraft.entity.Entity
import net.minecraft.world.Teleporter
import net.minecraft.world.WorldServer

class ThermiteTeleporter(world: WorldServer) : Teleporter(world) {

    fun placeInExistingPortal(e: Entity, x: Double, y: Double, z: Double, rY: Float): Boolean {
        e.setLocationAndAngles(x, y, z, rY, e.rotationPitch)
        return true
    }

    override fun removeStalePortalLocations(totalWorldTime: Long) {}

    fun placeInPortal(e: Entity, x: Double, y: Double, z: Double, rY: Float) {
        placeInExistingPortal(e, x, y, z, rY)
    }

}