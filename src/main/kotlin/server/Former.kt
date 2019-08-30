package server

import util.C
import java.io.File
import java.io.FileInputStream
import java.net.Socket

/**
 * Created by ordogod on 30.08.2019.
 **/

object Former {

    private const val DEFAULT_FILE = "index.html"

    fun clientIP(clientSocket: Socket) = clientSocket
        .remoteSocketAddress.toString()
        .removePrefix("/")
        .split(":")[0]

    fun methodName(rawName: String) = rawName.toUpperCase()

    fun dataName(rawName: String): String {
        return if (rawName == "/") "/$DEFAULT_FILE"
        else rawName
    }

    fun contentType(dataName: String): String =
        with(dataName) {
            when {
                this.endsWith(".html") -> "text/html"
                this.endsWith(".css")  -> "text/css"
                this.endsWith(".ico")  -> "image/x-icon"
                this.endsWith(".js")   -> "text/javascript"
                this.endsWith(".ttf")  -> "application/font-sfnt"
                else                        -> "text/plain"
            }
        }

    fun data(file: File): Pair<ByteArray, Int> {
        val fileIn: FileInputStream
        val data = ByteArray(file.length().toInt())

        try {
            fileIn = FileInputStream(file)
            fileIn.read(data)
            fileIn.close()
        }
        catch (e: Exception) { e.printStackTrace() }

        return Pair(data, data.size)
    }

    fun data(dataNameReq: String): Pair<ByteArray, Int> {
        val data = when (dataNameReq) {
            "/data/clientsConnected" -> C.clientsConnected.toString()
            else                     -> "null"
        }.toByteArray()

        return Pair(data, data.size)
    }
}