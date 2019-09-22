package server

import server.MainServer.DEFAULT_FILE
import util.C
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.net.Socket

/**
 * Created by ordogod on 30.08.2019.
 **/

object Fabric {

    enum class HttpStatus(val code: Int) {
        OK(200),
        NOT_FOUND(404)
    }

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
                this.endsWith(".jpg")  -> "image/jpeg"
                this.endsWith(".png")  -> "image/png"
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

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun data(dataNameReq: String, clientIP: String): ByteArray =
        when (dataNameReq) {
            "/data/playersMax"   -> C.PLAYERS_MAX
            "/data/players"      -> C.players.size
            "/data/playersReady" -> C.PLAYERS_READY
            "/data/rounds"       -> C.rounds
            "/data/thisTurn"     -> Model.findPlayer(clientIP)?.isTurn
            "/data/thisLossDist" -> Model.findPlayer(clientIP)?.lossDist
            "/data/thatLossDist" -> C.players.find { it.ip != clientIP }?.lossDist
            "/data/isHost"       -> C.players[0].ip == clientIP
            else                 -> "Fuck, CJ, here we go again?!"
        }.toString().toByteArray()

    fun decodePOST(inReader: BufferedReader): String {
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