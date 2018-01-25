package kettle.updater

import java.lang.ref.Reference
import java.lang.ref.WeakReference

import kettle.Kettle
import kettle.updater.TVersionRetriever.IVersionCheckCallback

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
        sender?.sendMessage(ChatColor.RED.toString() + "[Kettle] " + ChatColor.GRAY + "Kettle is up-to-date: " + Kettle.currentVersion)
        DefaultUpdateCallback.INSTANCE.upToDate()
    }

    override fun newVersion(newVersion: String) {
        val sender = mSender.get()
        if (sender != null) {
            newVersion(sender, Kettle.currentVersion.toString(), newVersion)
        }
        DefaultUpdateCallback.INSTANCE.newVersion(newVersion)
    }

    override fun error(t: Throwable) {
        val sender = mSender.get()
        sender?.sendMessage(ChatColor.RED.toString() + "[Kettle] " + ChatColor.DARK_RED + "Error ocurred durring version check, see details in server log.")
    }

    companion object {

        fun newVersion(sender: CommandSender, currentVersion: String,
                       newVersion: String) {
            sender.sendMessage(arrayOf(ChatColor.RED.toString() + "[Kettle] " + ChatColor.GRAY + "Found new version of Kettle: " + newVersion, ChatColor.RED.toString() + "[Kettle] " + ChatColor.GRAY + "Current version is: " + currentVersion, ChatColor.RED.toString() + "[Kettle] " + ChatColor.GREEN + "Download at: https://github.com/aolko/Kettle/releases"))
        }
    }
}
