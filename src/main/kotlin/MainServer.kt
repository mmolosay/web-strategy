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
    private const val DEFAULT_HTML = "index.html"
    private const val DEFAULT_CSS = "style.css"
    private val DEFAULT_ROOT =
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

            clientSocket = serverSocket.accept()

            input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            output = PrintWriter(clientSocket.getOutputStream())
            dataOut = BufferedOutputStream(clientSocket.getOutputStream())

            Log.i("${Date()}: ${clientSocket.remoteSocketAddress.toString().removePrefix("/")} connected.")

            val inputString = input.readLine()
            val parse = StringTokenizer(inputString)
            val method = parse.nextToken().toUpperCase()

            fileRequested = parse.nextToken().toLowerCase()

            if (method == "GET" || method == "HEAD") {
                when (fileRequested) {
                    "/" -> fileRequested = "/$DEFAULT_HTML"
                    DEFAULT_CSS -> fileRequested = "/$DEFAULT_CSS"
                    // TODO: change when to fileRequested = "/$fileRequested"
                }
                file = File(DEFAULT_ROOT, fileRequested)
                fileSize = file.length().toInt()
                content = contentType(fileRequested)

                if (method == "GET") {
                    var fileData = readFileData(file, fileSize)

                    output.println("HTTP/1.1 200 OK")
                    output.println("Server: Java HTTP Server from ordogod : 1.0")
                    output.println("Date: ${Date()}")
                    output.println("Content-type: $content")
                    output.println("Content-length: $fileSize")
                    output.println() // never remove!
                    output.flush()

                    dataOut.write(fileData, 0, fileSize)
                    dataOut.flush()

//                    file = File(DEFAULT_ROOT, "style.css")
//                    fileSize = file.length().toInt()
//                    content = "text/css"
//                    fileData = readFileData(file, fileSize)
//                    dataOut.write(fileData, 0, fileSize)
//                    dataOut.flush()
                }
            }
        }
        catch (e: Exception) {
            if (e is IOException) Log.f("Can not start server at port $port.")

            e.printStackTrace()
        }
    }

    private fun contentType(fileRequested: String): String {
        return if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
            "text/html"
        else if (fileRequested.endsWith(".css"))
            "text/css"
        else
            "text/plain"
    }

    private fun readFileData(file: File, length: Int): ByteArray {
        val fileIn: FileInputStream
        val fileData = ByteArray(length)

        try {
            fileIn = FileInputStream(file)
            fileIn.read(fileData)
            fileIn.close()
        }
        catch (e: Exception) { e.printStackTrace() }

        return fileData
    }
}