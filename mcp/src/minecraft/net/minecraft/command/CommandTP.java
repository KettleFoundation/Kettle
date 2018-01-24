package net.minecraft.command;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class CommandTP extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getName()
    {
        return "tp";
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
        return "commands.tp.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.tp.usage", new Object[0]);
        }
        else
        {
            int i = 0;
            Entity entity;

            if (args.length != 2 && args.length != 4 && args.length != 6)
            {
                entity = getCommandSenderAsPlayer(sender);
            }
            else
            {
                entity = getEntity(server, sender, args[0]);
                i = 1;
            }

            if (args.length != 1 && args.length != 2)
            {
                if (args.length < i + 3)
                {
                    throw new WrongUsageException("commands.tp.usage", new Object[0]);
                }
                else if (entity.world != null)
                {
                    int j = 4096;
                    int k = i + 1;
                    CommandBase.CoordinateArg commandbase$coordinatearg = parseCoordinate(entity.posX, args[i], true);
                    CommandBase.CoordinateArg commandbase$coordinatearg1 = parseCoordinate(entity.posY, args[k++], -4096, 4096, false);
                    CommandBase.CoordinateArg commandbase$coordinatearg2 = parseCoordinate(entity.posZ, args[k++], true);
                    CommandBase.CoordinateArg commandbase$coordinatearg3 = parseCoordinate((double)entity.rotationYaw, args.length > k ? args[k++] : "~", false);
                    CommandBase.CoordinateArg commandbase$coordinatearg4 = parseCoordinate((double)entity.rotationPitch, args.length > k ? args[k] : "~", false);
                    teleportEntityToCoordinates(entity, commandbase$coordinatearg, commandbase$coordinatearg1, commandbase$coordinatearg2, commandbase$coordinatearg3, commandbase$coordinatearg4);
                    notifyCommandListener(sender, this, "commands.tp.success.coordinates", new Object[] {entity.getName(), commandbase$coordinatearg.getResult(), commandbase$coordinatearg1.getResult(), commandbase$coordinatearg2.getResult()});
                }
            }
            else
            {
                Entity entity1 = getEntity(server, sender, args[args.length - 1]);

                if (entity1.world != entity.world)
                {
                    throw new CommandException("commands.tp.notSameDimension", new Object[0]);
                }
                else
                {
                    entity.dismountRidingEntity();

                    if (entity instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP)entity).connection.setPlayerLocation(entity1.posX, entity1.posY, entity1.posZ, entity1.rotationYaw, entity1.rotationPitch);
                    }
                    else
                    {
                        entity.setLocationAndAngles(entity1.posX, entity1.posY, entity1.posZ, entity1.rotationYaw, entity1.rotationPitch);
                    }

                    notifyCommandListener(sender, this, "commands.tp.success", new Object[] {entity.getName(), entity1.getName()});
                }
            }
        }
    }

    /**
     * Teleports an entity to the specified coordinates
     */
    private static void teleportEntityToCoordinates(Entity teleportingEntity, CommandBase.CoordinateArg argX, CommandBase.CoordinateArg argY, CommandBase.CoordinateArg argZ, CommandBase.CoordinateArg argYaw, CommandBase.CoordinateArg argPitch)
    {
        if (teleportingEntity instanceof EntityPlayerMP)
        {
            Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class);

            if (argX.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.X);
            }

            if (argY.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.Y);
            }

            if (argZ.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.Z);
            }

            if (argPitch.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.X_ROT);
            }

            if (argYaw.isRelative())
            {
                set.add(SPacketPlayerPosLook.EnumFlags.Y_ROT);
            }

            float f = (float)argYaw.getAmount();

            if (!argYaw.isRelative())
            {
                f = MathHelper.wrapDegrees(f);
            }

            float f1 = (float)argPitch.getAmount();

            if (!argPitch.isRelative())
            {
                f1 = MathHelper.wrapDegrees(f1);
            }

            teleportingEntity.dismountRidingEntity();
            ((EntityPlayerMP)teleportingEntity).connection.setPlayerLocation(argX.getAmount(), argY.getAmount(), argZ.getAmount(), f, f1, set);
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
        return args.length != 1 && args.length != 2 ? Collections.emptyList() : getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
