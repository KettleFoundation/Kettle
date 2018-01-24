package thermos.updater

import java.lang.ref.Reference
import java.lang.ref.WeakReference

import thermos.Thermos
import thermos.updater.TVersionRetriever.IVersionCheckCallback

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class CommandSenderUpdateCallback(sender: CommandSender) : IVersionCheckCallback {
    private val mSender: Reference<CommandSender>

    protected val sender: CommandSender?
        get() = mSender.get()

    init {
        mSender = WeakReference(sender)
    }

    override fun upToDate() {
        val sender = mSender.get()
        sender?.sendMessage(ChatColor.RED.toString() + "[Thermos] " + ChatColor.GRAY + "Thermos is up-to-date: " + Thermos.getCurrentVersion())
        DefaultUpdateCallback.INSTANCE.upToDate()
    }

    override fun newVersion(newVersion: String) {
        val sender = mSender.get()
        if (sender != null) {
            newVersion(sender, Thermos.getCurrentVersion(), newVersion)
        }
        DefaultUpdateCallback.INSTANCE.newVersion(newVersion)
    }

    override fun error(t: Throwable) {
        val sender = mSender.get()
        sender?.sendMessage(ChatColor.RED.toString() + "[Thermos] " + ChatColor.DARK_RED + "Error ocurred durring version check, see details in server log.")
    }

    companion object {

        fun newVersion(sender: CommandSender, currentVersion: String,
                       newVersion: String) {
            sender.sendMessage(arrayOf(ChatColor.RED.toString() + "[Thermos] " + ChatColor.GRAY + "Found new version of Thermos: " + newVersion, ChatColor.RED.toString() + "[Thermos] " + ChatColor.GRAY + "Current version is: " + currentVersion, ChatColor.RED.toString() + "[Thermos] " + ChatColor.GREEN + "Download at: https://github.com/CyberdyneCC/Thermos/releases"))
        }
    }
}
