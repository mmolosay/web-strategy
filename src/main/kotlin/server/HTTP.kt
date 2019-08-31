package server

import java.io.BufferedOutputStream
import java.io.PrintWriter

/**
 * Created by ordogod on 30.08.2019.
 **/

object HTTP {

    val REQUEST_IS_CONNECTED = "/connection"

    fun sendResponse(contentType: String, contentByteSize: Int, data: ByteArray,
                     resWriter: PrintWriter, dataWriter: BufferedOutputStream)
    {
        resWriter.println("HTTP/1.1 200 OK")
        resWriter.println("Server: Kotlin HTTP Server by ordogod : 1.0")
        resWriter.println("Content-type: $contentType")
        resWriter.println("Content-length: $contentByteSize")
        resWriter.println()
        resWriter.flush()

        with(dataWriter) {
            this.write(data, 0, contentByteSize)
            this.flush()
        }
    }

    fun sendRequest(reqWriter: PrintWriter, request: String) {
        reqWriter.println("GET $request HTTP/1.1")
        reqWriter.println("Connection: keep-alive")
        reqWriter.println()
        reqWriter.flush()
    }

//    fun notifyClients() {
//    }
}