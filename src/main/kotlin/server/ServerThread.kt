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

            val parse = StringTokenizer(inReader.readLine())

            val methodNameReq  = Former.methodName(parse.nextToken())
            val dataNameReq    = Former.dataName(parse.nextToken())
            val contentTypeRes = Former.contentType(dataNameReq)

            if (!C.findPlayer(clientIP) && contentTypeRes == "text/event-stream") {
                if (C.players.size < 2) {
                    val player = PlayerThread(clientSocket, clientIP)
                    C.addPlayer(player)
                    player.start()
                }
                else {
                    Responder(clientSocket)
                        .contentType("text/plain")
                        .data(C.INFO_NO_SLOTS_LEFT)
                        .send(true)
                }
            }

            if (methodNameReq == "GET") {

                Log.i("${C.beautyDate()}: $clientIP requested \'$dataNameReq\'.")

                var data: ByteArray = ByteArray(0)
                when {
                    contentTypeRes == "text/event-stream" -> {
                        Responder(clientSocket)
                            .contentType(contentTypeRes)
                            .data(data)
                            .send(true)
                    }
                    contentTypeRes != "text/plain" ->
                        data = Former.data(File(MainServer.DEFAULT_ROOT, dataNameReq))

                    else -> data = Former.data(dataNameReq)
                }

                Responder(clientSocket)
                    .contentType(contentTypeRes)
                    .data(data)
                    .send(false)
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