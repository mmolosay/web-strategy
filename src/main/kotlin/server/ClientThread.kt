package server

import server.MainServer.DEFAULT_ROOT
import server.MainServer.NO_SLOTS_FILE
import util.C
import java.io.*
import java.net.Socket
import java.net.SocketException
import java.util.*

/**
 * Created by ordogod on 29.08.2019.
 **/

class ClientThread(private val clientSocket: Socket) : Thread() {

    private val clientIP = Fabric.ip(clientSocket)

    override fun run() {
        try {
            val inReader   = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

            val parse = StringTokenizer(inReader.readLine())

            val methodNameReq  = Fabric.methodName(parse.nextToken())
            val dataNameReq    = Fabric.dataName(parse.nextToken())
            val contentTypeRes = Fabric.contentType(dataNameReq)

            if (!C.hasPlayer(clientIP)) {
                if (C.players.size < C.MAX_PLAYERS) {
                    with (PlayerThread(clientSocket, clientIP)) {
                        C.addPlayer(this)
                        start()
                    }
                }
                else if (dataNameReq == "/index.html")
                    Response(clientSocket)
                        .contentType("text/html")
                        .data(Fabric.data(File(DEFAULT_ROOT, NO_SLOTS_FILE)))
                        .send(false)
            }

            when (methodNameReq) {
                "GET" -> {
                    Log.i("$clientIP requested \'$dataNameReq\'.")

                    val data = when {
                        contentTypeRes != "text/plain" -> {
                            Fabric.data(File(DEFAULT_ROOT, dataNameReq))
                        }
                        else -> Fabric.data(dataNameReq, clientIP)
                    }

                    Response(clientSocket)
                        .contentType(contentTypeRes)
                        .data(data)
                        .send(true)
                }

                "POST" -> {
                    val data = Fabric.decodeData(inReader)

                    Log.i("$clientIP posted \'$data\'.")

                    when (data) {
                        "isConnected=true" -> C.findPlayer(clientIP)?.resetTimer()
                        "isReady=true"     -> C.findPlayer(clientIP)?.isReady = true
                        "isReady=false"    -> C.findPlayer(clientIP)?.isReady = false
                    }
                    with (data) {
                        when {
                            this.contains("rounds=") -> C.rounds = data.split("=")[1].toInt()
                        }
                    }

                    Response(clientSocket)
                        .data("Ponyatno".toByteArray())
                        .send(true)
                }
            }
        }
        catch (e: Exception) {
            if (e !is SocketException) { return }
            e.printStackTrace()
        }

        clientSocket.close()
    }
}