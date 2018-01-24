package kettle

import org.bukkit.configuration.file.YamlConfiguration

import net.minecraft.server.MinecraftServer
import net.minecraftforge.cauldron.configuration.BoolSetting
import net.minecraftforge.cauldron.configuration.ConfigBase
import net.minecraftforge.cauldron.configuration.Setting
import net.minecraftforge.cauldron.configuration.StringSetting

class ThermosConfig : ConfigBase("thermos.yml", "thermos") {
    var commandEnable = BoolSetting(this, "command.enable", true, "Enable Thermos command")
    var opConsoleOnly = BoolSetting(this, "op.consoleonly", false, "Set the OP command to only be allowed to run in console")
    var updatecheckerEnable = BoolSetting(this, "updatechecker.enable", true, "Enable Thermos update checker")
    var updatecheckerQuiet = BoolSetting(this, "updatechecker.quiet", false, "Print less info during update")

    var loggingMaterialInjection = BoolSetting(this, "logging.materialInjection", false, "Log material injection event")
    var loggingClientModList = BoolSetting(this, "logging.clientModList", true, "Print client's mod list during attempt to join")

    var commonAllowNetherPortal = BoolSetting(this, "common.allowNetherPortalBesidesOverworld", false, "Allow nether portals in dimensions besides overworld")

    init {
        register(commandEnable)
        register(updatecheckerEnable)
        register(updatecheckerQuiet)
        register(loggingMaterialInjection)
        register(loggingClientModList)
        register(commonAllowNetherPortal)
        register(opConsoleOnly)
        load()
    }

    private fun register(setting: Setting<*>) {
        settings[setting.path] = setting
    }

    override fun registerCommands() {
        if (commandEnable.value) {
            super.registerCommands()
        }
    }

    protected override fun addCommands() {
        commands[commandName] = ThermosCommand()
    }

    protected fun load() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile)
            var header = ""
            for (toggle in settings.values()) {
                if (!toggle.description.equals(""))
                    header += ("Setting: " + toggle.path + " Default: "
                            + toggle.def + " # " + toggle.description + "\n")

                config.addDefault(toggle.path, toggle.def)
                settings.get(toggle.path).setValue(
                        config.getString(toggle.path))
            }
            config.options().header(header)
            config.options().copyDefaults(true)
            save()
        } catch (ex: Exception) {
            MinecraftServer.getServer().logSevere(
                    "Could not load " + this.configFile)
            ex.printStackTrace()
        }

    }
}