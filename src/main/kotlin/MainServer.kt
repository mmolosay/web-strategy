import util.Log
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.io.File

/**
 * Created by ordogod on 26.08.2019.
 **/

object MainServer : Runnable {

    const val DEFAULT_PORT = 8080
    private const val SERVER_INFO = "Server: Java HTTP Server by ordogod : 1.0"
    const val DEFAULT_FILE = "/"
    const val DEFAULT_HTML = "index.html"
    const val DEFAULT_CSS = "style.css"
    const val DEFAULT_FAVICON = "favicon.ico"
    val DEFAULT_ROOT =
        when (System.getProperty("os.name")) {
            "Windows 10" -> File("D:\\dev\\java\\projects\\web_strategy\\src\\main\\resources")
            "Linux" -> File("/root/game/src/main/resources")
            // TODO: add your own case if your system doesn't match ones higher
            else -> File(".")
        }

    private var port: Int = DEFAULT_PORT

    fun init(port: Int): MainServer = apply {
        this.port = port
    }

    override fun run() {
        val serverSocket: ServerSocket
        val clientSocket: Socket

        val input: BufferedReader
        val output: PrintWriter

        val dataOut: BufferedOutputStream

        var fileRequested: String
        var file: File
        var fileSize: Int
        var content: String

        try {
            serverSocket = ServerSocket(port)
            Log.s("Server successfully started at port $port.")

            while (true) {
                ServerThread(serverSocket.accept()).start()
            }
        }
        catch (e: Exception) {
            if (e is IOException) Log.f("Can not start server at port $port.")

            e.printStackTrace()
        }
    }

    fun contentType(fileRequested: String): String {
        return with(fileRequested) {
            when {
                this.endsWith(".html") -> "text/html"
                this.endsWith(".css") -> "text/css"
                else -> "text/plain"
            }
        }
    }

    fun readFileData(file: File): ByteArray {
        val fileIn: FileInputStream
        val fileData = ByteArray(file.length().toInt())

        try {
            fileIn = FileInputStream(file)
            fileIn.read(fileData)
            fileIn.close()
        }
        catch (e: Exception) { e.printStackTrace() }

        return fileData
    }

//    fun parseRequest(input: BufferedReader): Pair<String, String> {
//
//    }

    fun sendAnswer(data: Array<String>, out: PrintWriter) {
        // [ "code", "status", "content-type", "conent-length" ]
        out.println("HTTP/1.1 ${data[0]} ${data[1]}")
        out.println(SERVER_INFO)
        out.println("Date: ${Date()}")
        out.println("Content-type: ${data[2]}")
        out.println("Content-length: ${data[3]}")
        out.println()
        out.flush()
    }

}