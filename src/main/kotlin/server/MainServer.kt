package server

import java.io.*
import java.net.ServerSocket
import java.io.File
import kotlin.system.exitProcess

/**
 * Created by ordogod on 26.08.2019.
 **/

object MainServer : Thread() {

    private val DEFAULT_PORT = 8080

    val DEFAULT_FILE = "index.html"
    val NO_SLOTS_FILE = "no_slots.html"
    val DEFAULT_ROOT = File(System.getProperty("user.dir"), """/src/main/resources/""")

    var port: Int = DEFAULT_PORT

    override fun run() {
        val serverSocket: ServerSocket

        try {
            serverSocket = ServerSocket(port)
            Log.s("Server successfully started at port $port.")

            while (true)
                RequestThread(serverSocket.accept()).start()
        }
        catch (e: Exception) {
            if (e is IOException) Log.f("Can not start server at port $port.")
            e.printStackTrace()
            exitProcess(0)
        }
    }
}