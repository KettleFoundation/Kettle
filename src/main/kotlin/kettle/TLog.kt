package kettle

import org.apache.logging.log4j.Level

import net.minecraftforge.fml.common.FMLLog

class TLog(private val mTag: String) {

    fun log(level: Level, throwable: Throwable?, message: String,
            vararg args: Any) {
        var t: Throwable? = null
        if (throwable != null) {
            t = Throwable()
            t.initCause(throwable)
            t.fillInStackTrace()
        }
        FMLLog.log(mTag, level, t, String.format(message, *args))
    }

    fun warning(message: String, vararg args: Any) {
        log(Level.WARN, null, message, *args)
    }

    fun warning(throwable: Throwable, message: String,
                vararg args: Any) {
        log(Level.WARN, throwable, message, *args)
    }

    fun info(message: String, vararg args: Any) {
        log(Level.INFO, null, message, *args)
    }

    fun info(throwable: Throwable, message: String,
             vararg args: Any) {
        log(Level.INFO, throwable, message, *args)
    }

    companion object {
        private val DEFAULT_LOGGER = TLog("Thermos")

        fun get(): TLog {
            return DEFAULT_LOGGER
        }

        operator fun get(tag: String): TLog {
            return TLog("Thermos: " + tag)
        }
    }
}
