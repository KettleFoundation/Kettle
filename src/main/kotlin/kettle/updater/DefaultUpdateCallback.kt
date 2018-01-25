package kettle.updater

import kettle.Kettle
import kettle.KettleCommand
import kettle.updater.TVersionRetriever.IVersionCheckCallback
import net.minecraft.server.MinecraftServer

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

class DefaultUpdateCallback private constructor() : IVersionCheckCallback {

    private var mHasUpdate: Boolean = false
    private var mCurrentVersion: String? = null
    private var mNewVersion: String? = null

    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (hasPermission(player)) {
            if (mHasUpdate) {
                sendUpdate(player)
            }
        }
    }

    private fun hasPermission(player: CommandSender): Boolean {
        return player.hasPermission(KettleCommand.UPDATE) || player.isOp
    }

    private fun sendUpdate(player: CommandSender) {
        CommandSenderUpdateCallback.newVersion(player, mCurrentVersion.toString(), mNewVersion.toString())
    }

    override fun upToDate() {
        mHasUpdate = false
        mCurrentVersion = Kettle.currentVersion
        mNewVersion = null
    }

    override fun newVersion(newVersion: String) {
        mCurrentVersion = Kettle.currentVersion
        mNewVersion = newVersion
        if (!mHasUpdate) {
            Bukkit.getConsoleSender().sendMessage("New version of Kettle available: " + newVersion)
            Bukkit.getConsoleSender().sendMessage("Download at: https://github.com/aolko/Kettle/releases")
            for (player in Bukkit.getOnlinePlayers()) {
                if (hasPermission(player)) {
                    sendUpdate(player)
                }
            }
        }
        mHasUpdate = true
    }

    override fun error(t: Throwable) {

    }

    companion object {
        var INSTANCE: DefaultUpdateCallback

        init {
            INSTANCE = DefaultUpdateCallback()
        }
    }
}
