package kettle

import net.minecraftforge.fml.common.FMLCommonHandler
import java.io.File
import java.net.URLDecoder
import java.util.*

object Thermos {
    val sThermosThreadGroup = ThreadGroup("Thermos")

    private var sManifestParsed = false

    private var sCurrentVersion: String? = null

    val currentVersion: String?
        get() {
            parseManifest()
            return sCurrentVersion
        }

    private var sServerLocation: File? = null

    val serverLocation: File?
        get() {
            parseManifest()
            return sServerLocation
        }

    private var sServerHome: File? = null

    val serverHome: File
        get() {
            if (sServerHome == null) {
                val home = System.getenv("THERMOS_HOME")
                if (home != null) {
                    sServerHome = File(home)
                } else {
                    parseManifest()
                    sServerHome = sServerLocation!!.parentFile
                }
            }
            return sServerHome
        }

    private var sGroup: String? = null

    val group: String?
        get() {
            parseManifest()
            return sGroup
        }

    private var sBranch: String? = null

    val branch: String?
        get() {
            parseManifest()
            return sBranch
        }

    private var sChannel: String? = null

    val channel: String?
        get() {
            parseManifest()
            return sChannel
        }

    private var sLegacy: Boolean = false
    private var sOfficial: Boolean = false

    val isLegacy: Boolean
        get() {
            parseManifest()
            return sLegacy
        }

    val isOfficial: Boolean
        get() {
            parseManifest()
            return sOfficial
        }

    var sNewServerLocation: File? = null
    var sNewServerVersion: String? = null
    var sUpdateInProgress: Boolean = false

    private var sForgeRevision = 0

    private fun parseManifest() {
        if (sManifestParsed)
            return
        sManifestParsed = true

        try {
            val resources = Thermos::class.java.classLoader
                    .getResources("META-INF/MANIFEST.MF")
            val manifest = Properties()
            while (resources.hasMoreElements()) {
                val url = resources.nextElement()
                manifest.load(url.openStream())
                val version = manifest.getProperty("Thermos-Version")
                if (version != null) {
                    val path = url.path
                    var jarFilePath = path.substring(path.indexOf(":") + 1,
                            path.indexOf("!"))
                    jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8")
                    sServerLocation = File(jarFilePath)

                    sCurrentVersion = version
                    sGroup = manifest.getProperty("Thermos-Group")
                    sBranch = manifest.getProperty("Thermos-Branch")
                    sChannel = manifest.getProperty("Thermos-Channel")
                    sLegacy = java.lang.Boolean.parseBoolean(manifest.getProperty("Thermos-Legacy"))
                    sOfficial = java.lang.Boolean.parseBoolean(manifest.getProperty("Thermos-Official"))
                    break
                }
                manifest.clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun restart() {
        RestartCommand.restart(true)
    }

    fun lookupForgeRevision(): Int {
        if (sForgeRevision != 0) return sForgeRevision
        var revision = Integer.parseInt(System.getProperty("thermos.forgeRevision", "0"))
        if (revision != 0) return sForgeRevision = revision
        try {
            val p = Properties()
            p.load(Thermos::class.java
                    .getResourceAsStream("/fmlversion.properties"))
            revision = Integer.parseInt(p.getProperty(
                    "fmlbuild.build.number", "0").toString())
        } catch (e: Exception) {
        }

        if (revision == 0) {
            TLog.get().warning("Thermos: could not parse forge revision, critical error")
            FMLCommonHandler.instance().exitJava(1, false)
        }
        return sForgeRevision = revision
    }
}
