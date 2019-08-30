package server

import java.io.BufferedOutputStream
import java.io.PrintWriter
import java.util.*

/**
 * Created by ordogod on 30.08.2019.
 **/

object Responser {

    fun sendResponse(contentType: String, contentByteSize: Int, data: ByteArray,
                     resWriter: PrintWriter, dataWriter: BufferedOutputStream)
    {
        resWriter.println("HTTP/1.1 200 OK")
        resWriter.println("Server: Kotlin HTTP Server by ordogod : 1.0")
        resWriter.println("Date: ${Date()}")
        resWriter.println("Content-type: $contentType")
        resWriter.println("Content-length: $contentByteSize")
        resWriter.println()
        resWriter.flush()

        with(dataWriter) {
            this.write(data, 0, contentByteSize)
            this.flush()
        }
    }

//    fun notifyClients() {
//    }
}