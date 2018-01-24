package net.minecraft.command.server;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CommandTeleport extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getName()
    {
        return "teleport";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender)
    {
        return "commands.teleport.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 4)
        {
            throw new WrongUsageException("commands.teleport.usage", new Object[0]);
        }
        else
        {
            Entity entity = getEntity(server, sender, args[0]);

            if (entity.world != null)
            {
                int i = 4096;
                Vec3d vec3d = sender.getPositionVector();
                int j = 1;
                CommandBase.CoordinateArg commandbase$coordinatearg = parseCoordinate(vec3d.x, args[j++], true);
                CommandBase.CoordinateArg commandbase$coordinatearg1 = parseCoordinate(vec3d.y, args[j++], -4096, 4096, false);
                CommandBase.CoordinateArg commandbase$coordinatearg2 = parseCoordinate(vec3d.z, args[j++], true);
                Entity entity1 = sender.getCommandSenderEntity() == null ? entity : sender.getCommandSenderEntity();
                CommandBase.CoordinateArg commandbase$coordinatearg3 = parseCoordinate(args.length > j ? (double)entity1.rotationYaw : (double)entity.rotationYaw, args.length > j ? args[j] : "~", false);
                ++j;
                CommandBase.CoordinateArg commandbase$coordinatearg4 = parseCoordinate(args.length > j ? (double)entity1.rotationPitch : (double)entity.rotationPitch, args.length > j ? args[j] : "~", false);
                doTeleport(entity, commandbase$coordinatearg, commandbase$coordinatearg1, commandbase$coordinatearg2, commandbase$coordinatearg3, commandbase$coordinatearg4);
                notifyCommandListener(sender, this, "commands.teleport.success.coordinates", new Object[] {entity.getName(), commandbase$coordinatearg.getResult(), commandbase$coordinatearg1.getResult(), commandbase$coordinatearg2.getResult()});
            }
        }
    }

    /**
     * Perform the actual teleport
     *  
     * @param teleportingEntity the entity being teleported
     */
    private static void doTeleport(Entity teleportingEntity, CommandBase.CoordinateArg argX, CommandBase.CoordinateArg argY, CommandBase.CoordinateArg argZ, CommandBase.CoordinateArg argYaw, CommandBase.CoordinateArg argPitch)
    {
        if (teleportingEntity instanceof EntityPlayerMP)
        {
            Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class);
            float f = (float)argYaw.getAmount();

            if (argYaw.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.Y_ROT);
            }
            else
            {
                f = MathHelper.wrapDegrees(f);
            }

            float f1 = (float)argPitch.getAmount();

            if (argPitch.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.X_ROT);
            }
            else
            {
                f1 = MathHelper.wrapDegrees(f1);
            }

            teleportingEntity.dismountRidingEntity();
            ((EntityPlayerMP)teleportingEntity).connection.setPlayerLocation(argX.getResult(), argY.getResult(), argZ.getResult(), f, f1, set);
            teleportingEntity.setRotationYawHead(f);
        }
        else
        {
            float f2 = (float)MathHelper.wrapDegrees(argYaw.getResult());
            float f3 = (float)MathHelper.wrapDegrees(argPitch.getResult());
            f3 = MathHelper.clamp(f3, -90.0F, 90.0F);
            teleportingEntity.setLocationAndAngles(argX.getResult(), argY.getResult(), argZ.getResult(), f2, f3);
            teleportingEntity.setRotationYawHead(f2);
        }

        if (!(teleportingEntity instanceof EntityLivingBase) || !((EntityLivingBase)teleportingEntity).isElytraFlying())
        {
            teleportingEntity.motionY = 0.0D;
            teleportingEntity.onGround = true;
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        else
        {
            return args.length > 1 && args.length <= 4 ? getTabCompletionCoordinate(args, 1, targetPos) : Collections.emptyList();
        }
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
