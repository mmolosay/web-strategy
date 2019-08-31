package server

import java.io.PrintWriter
import java.net.Socket
import java.util.*

/**
 * Created by ordogod on 30.08.2019.
 **/

class PlayerThread(private val socket: Socket, val ip: String) : Thread() {

    private val failedConections = 0
    private val reqWriter = PrintWriter(socket.getOutputStream())

    override fun run() {

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                //
            }
        }, 5000, 5000)
    }
}