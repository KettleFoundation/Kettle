package thermos.updater

import java.io.InputStreamReader
import java.lang.Thread.UncaughtExceptionHandler

import thermos.Thermos
import thermos.TLog
import net.minecraft.server.MinecraftServer

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.LaxRedirectStrategy
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

class TVersionRetriever(private val mCallback: IVersionCheckCallback?, private val mLoop: Boolean,
                        private val mUpToDateSupport: Boolean, private val mGroup: String, private val mName: String) : Runnable, UncaughtExceptionHandler {
    private val mThread: Thread

    init {
        if (DEBUG)
            sLogger.info("Created new version retriever")
        mThread = Thread(Thermos.sThermosThreadGroup, this, "Thermos version retriever")
        mThread.priority = Thread.MIN_PRIORITY
        mThread.isDaemon = true
        mThread.uncaughtExceptionHandler = this
        mThread.start()
    }

    override fun run() {
        while (!mThread.isInterrupted) {
            check()
            if (!mLoop)
                break
            try {
                Thread.sleep((1000 * 60 * 10).toLong())// Sleep ten minutes
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    private fun check() {
        try {
            val request = RequestBuilder
                    .get()
                    .setUri("http://th.tcpr.ca/thermos/version")
                    .addParameter("version", Thermos.getCurrentVersion()).build()
            val response = HttpClientBuilder.create()
                    .setUserAgent("Thermos Version Retriever")
                    .setRedirectStrategy(LaxRedirectStrategy()).build()
                    .execute(request)
            if (response.statusLine.statusCode != 200) {
                uncaughtException(mThread, IllegalStateException(
                        "Status code isn't OK"))
                return
            }
            val json = sParser.parse(InputStreamReader(
                    response.entity.content)) as JSONObject
            val version = json["version"] as String
            if (!mUpToDateSupport || Thermos.getCurrentVersion() == null
                    || version != Thermos.getCurrentVersion()) {
                mCallback!!.newVersion(version)
            } else {
                mCallback!!.upToDate()
            }
        } catch (e: Exception) {
            uncaughtException(null, e)
        }

    }

    override fun uncaughtException(t: Thread?, e: Throwable) {
        sLogger.warning(e, "Error occured during retriving version")
        mCallback?.error(e)
    }

    interface IVersionCheckCallback {
        fun upToDate()

        fun newVersion(newVersion: String)

        fun error(t: Throwable)
    }

    companion object {
        private val DEBUG: Boolean
        private val sLogger: TLog
        private val sParser: JSONParser
        private var sServer: MinecraftServer? = null

        init {
            DEBUG = false
            sLogger = TLog.get(TVersionRetriever::class.java.simpleName)

            sParser = JSONParser()
        }

        fun init(server: MinecraftServer) {
            sServer = server
            if (MinecraftServer.thermosConfig.updatecheckerEnable.getValue()) {
                startServer(DefaultUpdateCallback.INSTANCE, true)
            }
        }

        fun startServer(callback: IVersionCheckCallback, loop: Boolean) {
            TVersionRetriever(callback, loop, true, Thermos.getGroup(),
                    Thermos.getChannel())
        }
    }
}
