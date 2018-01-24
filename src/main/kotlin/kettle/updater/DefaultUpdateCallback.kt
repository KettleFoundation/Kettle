package thermos.updater

import thermos.Thermos
import thermos.ThermosCommand
import thermos.updater.TVersionRetriever.IVersionCheckCallback
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
        return player.hasPermission(ThermosCommand.UPDATE) || player.isOp
    }

    private fun sendUpdate(player: CommandSender) {
        CommandSenderUpdateCallback.newVersion(player, mCurrentVersion, mNewVersion)
    }

    override fun upToDate() {
        mHasUpdate = false
        mCurrentVersion = Thermos.getCurrentVersion()
        mNewVersion = null
    }

    override fun newVersion(newVersion: String) {
        mCurrentVersion = Thermos.getCurrentVersion()
        mNewVersion = newVersion
        if (!mHasUpdate) {
            Bukkit.getConsoleSender().sendMessage("New version of Thermos available: " + newVersion)
            Bukkit.getConsoleSender().sendMessage("Download at: https://github.com/CyberdyneCC/Thermos/releases")
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
