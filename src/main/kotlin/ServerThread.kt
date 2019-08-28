import MainServer.DEFAULT_CSS
import MainServer.DEFAULT_FAVICON
import MainServer.DEFAULT_FILE
import MainServer.DEFAULT_HTML
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

    override fun run() {
        try {
            val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val output = PrintWriter(clientSocket.getOutputStream())
            val dataOut = BufferedOutputStream(clientSocket.getOutputStream())

            val parse = StringTokenizer(input.readLine())
            val method = parse.nextToken().toUpperCase()
            var fileRequested = parse.nextToken().toLowerCase()

            if (method == "GET") {
                Log.i("${Date()}: ${clientSocket.remoteSocketAddress.toString().removePrefix("/")}" +
                        " requests $fileRequested.")

                    when (fileRequested) {
                        DEFAULT_FILE -> fileRequested = "/$DEFAULT_HTML"
                        DEFAULT_CSS -> fileRequested = "/$DEFAULT_CSS"
                        DEFAULT_FAVICON -> fileRequested = "/$DEFAULT_FAVICON"
                        // TODO: change when to fileRequested = "/$fileRequested"
                    }
                    val file = File(DEFAULT_ROOT, fileRequested)
                    val fileSize = file.length().toInt()
                    val content = contentType(fileRequested)

                val fileData = MainServer.readFileData(file)

                sendAnswer(arrayOf(
                    "200", "OK", content, fileSize.toString()
                ), output)

                dataOut.write(fileData, 0, fileSize)
                dataOut.flush()
            }

            clientSocket.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}