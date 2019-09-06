package server

import server.MainServer.DEFAULT_FILE
import util.C
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.Socket

/**
 * Created by ordogod on 30.08.2019.
 **/

object Fabric {

    fun ip(clientSocket: Socket): String = clientSocket
        .remoteSocketAddress.toString()
        .removePrefix("/")
        .split(":")[0]

    fun methodName(rawName: String): String = rawName.toUpperCase()

    fun dataName(rawName: String): String =
        if (rawName == "/") "/$DEFAULT_FILE"
        else rawName

    fun contentType(dataName: String): String =
        with(dataName) {
            when {
                this.endsWith(".html") -> "text/html"
                this.endsWith(".htm")  -> "text/html"
                this.endsWith(".css")  -> "text/css"
                this.endsWith(".ico")  -> "image/x-icon"
                this.endsWith(".js")   -> "text/javascript"
                this.endsWith(".ttf")  -> "application/font-sfnt"
                this.endsWith(".jpg")  -> "image/gif"
                else                   -> "text/plain"
            }
        }

    fun data(file: File): ByteArray {
        val fileIn: FileInputStream
        val data = ByteArray(file.length().toInt())

        try {
            fileIn = FileInputStream(file)
            fileIn.read(data)
            fileIn.close()
        }
        catch (e: Exception) { e.printStackTrace() }

        return data
    }

    fun data(dataNameReq: String, clientIP: String): ByteArray =
        when (dataNameReq) {
            "/data/players"      -> C.players.size
            "/data/playersReady" -> C.playersReady()
            "/data/rounds"       -> C.rounds
            "/data/isHost"       -> if (C.players[0].ip == clientIP) "true" else "false"
            else                 -> "Fuck, CJ, here we go again?!"
        }.toString().toByteArray()

    fun decodePOST(fromSocket: Socket): String {
        val inReader = BufferedReader(InputStreamReader(fromSocket.getInputStream()))
        var dataLength = 0
        val data: CharArray
        while (true) {
            val line = inReader.readLine().toLowerCase()
            if (line.contains("content-length"))
                dataLength = line.split(": ")[1].toInt()
            if (line == "") break
        }

        data = CharArray(dataLength)
        inReader.read(data, 0, dataLength)
        return String(data)
    }
}