package server

import util.C
import util.Log
import java.io.*
import java.net.ServerSocket
import java.util.*
import java.io.File

/**
 * Created by ordogod on 26.08.2019.
 **/

object MainServer : Runnable {

    const val DEFAULT_PORT = 8080
    const val DEFAULT_FILE = "index.html"
    val DEFAULT_ROOT = defaultRoot()

    private var port: Int = DEFAULT_PORT

    fun init(port: Int): MainServer = apply {
        MainServer.port = port
    }

    override fun run() {
        val serverSocket: ServerSocket

        try {
            serverSocket = ServerSocket(port)
            Log.s("Server successfully started at port $port.")

            while (true) {
                val clientThread = ServerThread(serverSocket.accept())
                if (C.clientsConnected < 2) clientThread.start()
                else Log.i("${Date()}: ${clientThread.clientIP}: connection refused, all players already connected")
            }
        }
        catch (e: Exception) {
            if (e is IOException) Log.f("Can not start server at port $port.")
            e.printStackTrace()
        }
    }

    private fun defaultRoot() =
        when (System.getProperty("os.name")) {
            "Windows 10" -> File("D:\\dev\\java\\projects\\web_strategy\\src\\main\\resources")
            "Linux"      -> File("/root/game/src/main/resources")
            else         -> File(".")
        }


    fun contentType(fileRequested: String): String {
        return with(fileRequested) {
            when {
                this.endsWith(".html") -> "text/html"
                this.endsWith(".css")  -> "text/css"
                this.endsWith(".ico")  -> "image/x-icon"
                this.endsWith(".js")   -> "text/javascript"
                this.endsWith(".ttf")  -> "application/font-sfnt"
                else                        -> "text/plain"
            }
        }
    }

    fun readFileData(file: File): ByteArray {
        val fileIn: FileInputStream
        val fileData = ByteArray(file.length().toInt())

        try {
            fileIn = FileInputStream(file)
            fileIn.read(fileData)
            fileIn.close()
        }
        catch (e: Exception) { e.printStackTrace() }

        return fileData
    }

    fun sendAnswer(data: Array<String>, out: PrintWriter) {
        // [ "code", "status", "content-type", "conent-length" ]
        out.println("HTTP/1.1 ${data[0]} ${data[1]}")
        out.println("Server: Kotlin HTTP Server by ordogod : 1.0")
        out.println("Date: ${Date()}")
        out.println("Content-type: ${data[2]}")
        out.println("Content-length: ${data[3]}")
        out.println()
        out.flush()
    }

    fun findIP(ip: String): Boolean {
        val i = C.clientsIPs.iterator()
        while (i.hasNext())
            if (i.next() == ip) return true
        return false
    }

    fun addIP(ip: String) {
        C.clientsIPs.add(ip)
        C.clientsConnected++
    }
}