package server

import util.C
import util.Log
import java.io.BufferedReader
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
            "/data/playersCount" -> C.players.size.toString()
            else                 -> "Fuck, CJ, here we go again!"
        }.toByteArray()

        return Pair(data, data.size)
    }

    fun POSTdata(inReader: BufferedReader): String {
        var dataLength = 0
        while (true) {
            val line = inReader.readLine().toLowerCase()
            if (line.contains("content-length")) {
                dataLength = line.split(" ")[1].toInt()
            }
            if (line == "") break
        }

        val data = CharArray(dataLength)
        inReader.read(data, 0, dataLength)
        return String(data)
    }
}