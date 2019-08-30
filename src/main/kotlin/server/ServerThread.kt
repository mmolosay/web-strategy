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

            if (!C.findIP(clientIP)) {
                if (C.clientsConnected < 2)
                    C.addIP(clientIP)
                else {
                    Responser.sendResponse(
                        "text/plain", C.INFO_NO_SLOTS_LEFT.size, C.INFO_NO_SLOTS_LEFT,
                        outWriter, dataWriter
                    )
                    clientSocket.close()
                }
            }

            if (methodNameReq == "GET") {

                Log.i("${Date()}: $clientIP requests \'$dataNameReq\'.")

                val data: Pair<ByteArray, Int> =
                    if (contentTypeRes != "text/plain") {
                        Former.data(File(MainServer.DEFAULT_ROOT, dataNameReq))
                    }
                    else {
                        Former.data(dataNameReq)
                    }

                Responser.sendResponse(contentTypeRes, data.second, data.first, outWriter, dataWriter)
            }

            clientSocket.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}