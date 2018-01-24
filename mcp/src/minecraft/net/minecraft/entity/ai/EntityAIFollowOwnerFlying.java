package net.minecraft.entity.ai;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.BlockPos;

public class EntityAIFollowOwnerFlying extends EntityAIFollowOwner
{
    public EntityAIFollowOwnerFlying(EntityTameable tameableIn, double followSpeedIn, float minDistIn, float maxDistIn)
    {
        super(tameableIn, followSpeedIn, minDistIn, maxDistIn);
    }

    protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset)
    {
        IBlockState iblockstate = this.world.getBlockState(new BlockPos(x + xOffset, y - 1, z + zOffset));
        return (iblockstate.isTopSolid() || iblockstate.getMaterial() == Material.LEAVES) && this.world.isAirBlock(new BlockPos(x + xOffset, y, z + zOffset)) && this.world.isAirBlock(new BlockPos(x + xOffset, y + 1, z + zOffset));
    }
}
