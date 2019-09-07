package server

import server.MainServer.DEFAULT_ROOT
import server.MainServer.NO_SLOTS_FILE
import util.C
import java.io.*
import java.net.Socket
import java.net.SocketException

/**
 * Created by ordogod on 29.08.2019.
 **/

class RequestThread(private val fromSocket: Socket) : Thread() {

    private val ip = Fabric.ip(fromSocket)

    override fun run() {
        try {
            val inReader = BufferedReader(InputStreamReader(fromSocket.getInputStream()))

            val reqHead = inReader.readLine().split(" ")

            val methodNameReq  = Fabric.methodName(reqHead[0])
            val dataNameReq    = Fabric.dataName(reqHead[1])
            val contentTypeRes = Fabric.contentType(dataNameReq)

            if (!C.containsPlayer(ip)) {
                if (C.players.size < C.MAX_PLAYERS) {
                    with (PlayerThread(fromSocket, ip)) {
                        C.addPlayer(this)
                        start()
                    }
                }
                else if (dataNameReq == "/index.html")
                    Response(fromSocket)
                        .contentType("text/html")
                        .data(Fabric.data(File(DEFAULT_ROOT, NO_SLOTS_FILE)))
                        .send(false)
            }

            when (methodNameReq) {
                "GET" -> {
                    Log.i("$ip requested \'$dataNameReq\'.")

                    val data =
                        if (contentTypeRes == "text/plain")
                            Fabric.data(dataNameReq, ip)
                        else
                            Fabric.data(File(DEFAULT_ROOT, dataNameReq))

                    Response(fromSocket)
                        .contentType(contentTypeRes)
                        .data(data)
                        .send(true)
                }

                "POST" -> {
                    val data = Fabric.decodePOST(inReader)
                    Log.i("$ip posted \'$data\'.")

                    Model.onReceive(data, ip)

                    Response(fromSocket)
                        .data("Ponyatno")
                        .send(true)
                }
            }
        }
        catch (e: SocketException) { e.printStackTrace() }

        fromSocket.close()
    }
}