package server

import java.io.BufferedOutputStream
import java.io.PrintWriter
import java.net.Socket
import java.util.*

/**
 * Created by ordogod on 31.08.2019.
 **/

class Response(private val socket: Socket) {

    data class HeaderPair<A, B>(var name: A, var value: B) {
        fun join() = "$name: $value"
    }

    private val outWriter  = PrintWriter(socket.getOutputStream())
    private val dataWriter = BufferedOutputStream(socket.getOutputStream())

    private var status = Pair(200, Fabric.HttpStatus.OK)
    private var data = byteArrayOf()
    private var headers = arrayListOf(

        HeaderPair("Date", Date().toString()),
        HeaderPair("Server", "Kotlin HTTP server by ordogod 1.0 Linux"),
        HeaderPair("Content-type", "text/plain"),
        HeaderPair("Content-length", "0"),
        HeaderPair("Connection", "close")

    )

    fun status(value: Fabric.HttpStatus): Response = apply {
        status = Pair(value.code, value)
    }

    private fun findHeaderOrAdd(name: String): HeaderPair<String, String> {
        for (header in headers) {
            if (header.name == name) return header
        }
        headers.add(HeaderPair(name, ""))
        return headers[headers.size - 1]
    }

    fun <T> header(name: String, value: T): Response = apply {
        val header = findHeaderOrAdd(name)
        header.value = value.toString()
    }

    fun removeHeader(name: String): Response = apply {
        for (header in headers) {
            if (header.name == name) headers.remove(header)
        }
    }

    fun clearHeaders(): Response = apply {
        headers.clear()
    }

    fun contentType(type: String): Response = apply {
        header("Content-type", type)
    }

    fun data(data: ByteArray, setContentLength: Boolean = true): Response = apply {
        this.data = data
        if (setContentLength) header("Content-length", this.data.size)
    }

    fun data(data: String, setContentLength: Boolean = true): Response = apply {
        this.data = data.toByteArray()
        contentType("text/plain")
        if (setContentLength) header("Content-length", this.data.size)
    }

    fun send(closeSocket: Boolean = false) {
        try {
            outWriter.println("HTTP/1.1 ${status.first} ${status.second}")
            for (header in headers) outWriter.println(header.join())
            outWriter.println()
            outWriter.flush()

            dataWriter.write(data)
            dataWriter.flush()
        }
        catch (e: Exception) { e.printStackTrace() }
        finally { if (closeSocket) socket.close() }
    }
}