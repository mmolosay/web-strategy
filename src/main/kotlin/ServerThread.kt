import MainServer.DEFAULT_ROOT
import MainServer.contentType
import MainServer.sendAnswer
import util.Log
import java.io.*
import java.net.Socket
import java.util.*

/**
 * Created by ordogod on 29.08.2019.
 **/

class ServerThread(private val clientSocket: Socket) : Thread() {

    private val clientIP = clientSocket.remoteSocketAddress.toString().removePrefix("/")

    override fun run() {
        try {
            val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val output = PrintWriter(clientSocket.getOutputStream())
            val dataOut = BufferedOutputStream(clientSocket.getOutputStream())

            val parse = StringTokenizer(input.readLine())

            val methodRequest = parse.nextToken().toUpperCase()
            val fileRequest = "/${parse.nextToken().toLowerCase()}"

            if (methodRequest == "GET") {
                Log.i("${Date()}: $clientIP requests \'$fileRequest\'.")

                val file = File(DEFAULT_ROOT, fileRequest)
                val fileSize = file.length().toInt()
                val content = contentType(fileRequest)

                sendAnswer(arrayOf(
                    "200", "OK", content, fileSize.toString()
                ), output)

                with(dataOut) {
                    this.write(MainServer.readFileData(file), 0, fileSize)
                    this.flush()
                }
            }

            clientSocket.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}