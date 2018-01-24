package kettle

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer

import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer

import thermos.updater.CommandSenderUpdateCallback
import thermos.updater.TVersionRetriever
import net.minecraft.entity.Entity
import net.minecraft.server.MinecraftServer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.NextTickListEntry
import net.minecraft.world.WorldServer
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.DimensionManager.getWorlds
import net.minecraftforge.fml.common.FMLCommonHandler

class ThermosCommand : Command(NAME) {
    init {

        val builder = StringBuilder()
        builder.append(String.format("-------------------[" + ChatColor.RED + "Thermos" + ChatColor.RESET + "]-------------------\n"))
        builder.append(String.format("/%s check - Check for an update.\n", NAME))
        builder.append(String.format("/%s tps - Show tps statistics.\n", NAME))
        builder.append(String.format("/%s restart - Restart the server.\n", NAME))
        builder.append(String.format("/%s dump - Dump statistics into a thermos.dump file.\n", NAME))
        usage = builder.toString()

        permission = "thermos"
    }

    fun testPermission(target: CommandSender, permission: String): Boolean {
        if (testPermissionSilent(target, permission)) {
            return true
        }
        target.sendMessage(ChatColor.RED.toString() + "[Thermos] " + ChatColor.DARK_RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is an error.")
        return false
    }

    fun testPermissionSilent(target: CommandSender, permission: String): Boolean {
        if (!super.testPermissionSilent(target)) {
            return false
        }
        for (p in permission.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            if (target.hasPermission(p))
                return true
        return false
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (!testPermission(sender))
            return true
        if (args.size == 0) {
            sender.sendMessage(ChatColor.RED.toString() + "[Thermos] " + ChatColor.GRAY + "Please specify action")
            sender.sendMessage(ChatColor.RED.toString() + "[Thermos] " + ChatColor.GRAY + usageMessage)
            return true
        }
        val action = args[0]
        if ("check" == action) {
            if (!testPermission(sender, CHECK))
                return true
            sender.sendMessage(ChatColor.RED.toString() + "[Thermos] " + ChatColor.GRAY + "Initiated version check...")
            TVersionRetriever.startServer(CommandSenderUpdateCallback(sender), false)
        } else if ("tps" == action) {
            if (!testPermission(sender, TPS))
                return true
            var currentWorld: World? = null
            if (sender is CraftPlayer) {
                currentWorld = (sender as CraftPlayer).getWorld()
            }
            sender.sendMessage(ChatColor.DARK_RED.toString() + "---------------------------------------")
            val server = FMLCommonHandler.instance().minecraftServerInstance
            var colourTPS: ChatColor
            for (world in server.worlds) {
                if (world is CraftWorld) {
                    val current = currentWorld != null && currentWorld === world
                    val mcWorld = (world as CraftWorld).getHandle()
                    val bukkitName = world.getName()
                    val dimensionId = mcWorld.provider.dimensionType.id
                    val name = mcWorld.provider.dimensionType.getName()
                    val displayName = if (name == bukkitName) name else String.format("%s | %s", name, bukkitName)
                    val worldTickTime = mean(server.worldTickTimes[dimensionId]!!) * 1.0E-6
                    val worldTPS = Math.min(1000.0 / worldTickTime, 20.0)

                    if (worldTPS >= 18.0) {
                        colourTPS = ChatColor.GREEN
                    } else if (worldTPS >= 15.0) {
                        colourTPS = ChatColor.YELLOW
                    } else {
                        colourTPS = ChatColor.RED
                    }

                    sender.sendMessage(String.format("%s[%d] %s%s %s- %s%.2fms / %s%.2ftps", ChatColor.GOLD, dimensionId,
                            if (current) ChatColor.GREEN else ChatColor.YELLOW, displayName, ChatColor.RESET,
                            ChatColor.DARK_RED, worldTickTime, colourTPS, worldTPS))
                }
            }

            val meanTickTime = mean(server.tickTimeArray) * 1.0E-6
            val meanTPS = Math.min(1000.0 / meanTickTime, 20.0)
            if (meanTPS >= 18.0) {
                colourTPS = ChatColor.GREEN
            } else if (meanTPS >= 15.0) {
                colourTPS = ChatColor.YELLOW
            } else {
                colourTPS = ChatColor.RED
            }
            sender.sendMessage(String.format("%sOverall - %s%s%.2fms / %s%.2ftps", ChatColor.BLUE, ChatColor.RESET,
                    ChatColor.DARK_RED, meanTickTime, colourTPS, meanTPS))
            sender.sendMessage(ChatColor.DARK_RED.toString() + "---------------------------------------")
        } else if ("restart" == action) {
            if (!testPermission(sender, RESTART))
                return true
            Thermos.restart()
        } else if ("dump" == action) {
            if (!testPermission(sender, DUMP))
                return true
            try {
                val outputFile = File("thermos.dump")
                val os = FileOutputStream(outputFile)
                val writer = OutputStreamWriter(os)
                for (world in DimensionManager.getWorlds()) {
                    writer.write(String.format("Stats for %s [%s] with id %d\n", world,
                            world.provider.dimensionType.getName(), world.provider.dimensionType.id))
                    writer.write("Current tick: " + world.worldInfo.worldTotalTime + "\n")
                    writer.write("\nEntities: ")
                    writer.write("count - " + world.loadedEntityList.size + "\n")
                    for (entity in world.loadedEntityList as Iterable<Entity>) {
                        writer.write(String.format("  %s at (%.4f;%.4f;%.4f)\n", entity.javaClass.name,
                                entity.posX, entity.posY, entity.posZ))
                    }
                    writer.write("\nTileEntities: ")
                    writer.write("count - " + world.loadedTileEntityList.size + "\n")
                    for (entity in world.loadedTileEntityList as Iterable<TileEntity>) {
                        writer.write(String.format("  %s at (%d;%d;%d)\n", entity.javaClass.name, entity.pos.x,
                                entity.pos.y, entity.pos.z))
                    }
                    writer.write("\nLoaded chunks: ")
                    writer.write("count - " + world.chunkProvider.loadedChunkCount + "\n")
                    for (chunkCoords in world.chunkProvider.loadedChunks as Iterable<Chunk>) {
                        val x = chunkCoords.pos.x
                        val z = chunkCoords.pos.z
                        val chunk = world.chunkProvider.provideChunk(x, z) ?: continue
                        writer.write(String.format("Chunk at (%d;%d)\n", x, z))
                        val updates = world.getPendingBlockUpdates(chunk, false)
                        writer.write("Pending block updates [" + updates!!.size + "]:\n")
                        for (entry in updates) {
                            writer.write(String.format("(%d;%d;%d) at %d with priority %d\n", entry.position.x,
                                    entry.position.y, entry.position.z, entry.scheduledTime, entry.priority))
                        }
                    }
                    writer.write("-------------------------\n")
                }
                writer.close()
                sender.sendMessage(ChatColor.RED.toString() + "Dump saved!")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            sender.sendMessage(ChatColor.RED.toString() + "Unknown action")
        }
        return true
    }

    companion object {
        val NAME = "thermos"
        val CHECK = NAME + ".check"
        val TPS = NAME + ".tps"
        val RESTART = NAME + ".restart"
        val DUMP = NAME + ".dump"
        val UPDATE = NAME + ".update"

        private fun mean(array: LongArray): Long {
            var r: Long = 0
            for (i in array)
                r += i
            return r / array.size
        }
    }

}
