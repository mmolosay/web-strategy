package server

import server.MainServer.DEFAULT_FILE
import server.MainServer.DEFAULT_ROOT
import server.MainServer.addIP
import server.MainServer.contentType
import server.MainServer.findIP
import server.MainServer.sendAnswer
import util.C
import util.C.THIN_SEPARATOR
import util.C.clientsConnected
import util.Log
import java.io.*
import java.net.Socket
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture





/**
 * Created by ordogod on 29.08.2019.
 **/

class ServerThread(private val clientSocket: Socket) : Thread() {

    val clientIP = clientSocket.remoteSocketAddress.toString().removePrefix("/").split(":")[0]

    override fun run() {
        try {
            if (!findIP(clientIP)) addIP(clientIP)

            val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val output = PrintWriter(clientSocket.getOutputStream())
            val dataOut = BufferedOutputStream(clientSocket.getOutputStream())

            val parse = StringTokenizer(input.readLine())

            val methodRequest = parse.nextToken().toUpperCase()
            var request = parse.nextToken()

            if (request == "/") {
                request = "/$DEFAULT_FILE"
                Log.i(THIN_SEPARATOR)
            }

            if (methodRequest == "GET") {
                Log.i("${Date()}: $clientIP requests \'$request\'.")

                val content = contentType("/$request")

                if (content != "text/plain") {
                    val file = File(DEFAULT_ROOT, "/$request")
                    val fileSize = file.length().toInt()

                    sendAnswer(
                        arrayOf(
                            "200", "OK", content, fileSize.toString()
                        ), output
                    )

                    with(dataOut) {
                        this.write(MainServer.readFileData(file), 0, fileSize)
                        this.flush()
                    }
                }
                else {
                    val data = when (request) {
                        "/data/clientsConnected" -> C.clientsConnected.toString()
                        else -> "null"
                    }

                    val bytes = data.toByteArray()

                    sendAnswer(
                        arrayOf(
                            "200", "OK", content, bytes.size.toString()
                        ), output
                    )

                    with(dataOut) {
                        this.write(bytes, 0, bytes.size)
                        this.flush()
                    }
                }
            }

            clientSocket.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}