package server

import util.C
import util.Log
import java.io.*
import java.net.Socket
import java.util.*

/**
 * Created by ordogod on 29.08.2019.
 **/

class ServerThread(private val clientSocket: Socket) : Thread() {

    private val clientIP = Former.clientIP(clientSocket)

    override fun run() {
        try {
            val inReader   = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val outWriter  = PrintWriter(clientSocket.getOutputStream())
            val dataWriter = BufferedOutputStream(clientSocket.getOutputStream())

            val parse = StringTokenizer(inReader.readLine())

            val methodNameReq  = Former.methodName(parse.nextToken())
            val dataNameReq    = Former.dataName(parse.nextToken())
            val contentTypeRes = Former.contentType(dataNameReq)

            if (!C.findPlayer(clientIP)) {
                if (C.players.size < 2) {
                    val player = PlayerThread(clientSocket, clientIP)
                    C.addPlayer(player)
                    player.start()
                }
                else {
                    HTTP.sendResponse(
                        "text/plain", C.INFO_NO_SLOTS_LEFT.size, C.INFO_NO_SLOTS_LEFT,
                        outWriter, dataWriter
                    )
                    clientSocket.close()
                }
            }

            if (methodNameReq == "GET") {

                Log.i("${Date()}: $clientIP requested \'$dataNameReq\'.")

                val data: Pair<ByteArray, Int> =
                    if (contentTypeRes != "text/plain") {
                        Former.data(File(MainServer.DEFAULT_ROOT, dataNameReq))
                    }
                    else {
                        Former.data(dataNameReq)
                    }

                HTTP.sendResponse(contentTypeRes, data.second, data.first, outWriter, dataWriter)
            }

            if (methodNameReq == "POST") {

                val data = Former.POSTdata(inReader)

                Log.i("${Date()}: $clientIP posted \'$data\'.")
                MainServer.onDataArrived(data, clientIP)
            }

            clientSocket.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}