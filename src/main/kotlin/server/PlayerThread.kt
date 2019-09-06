package server

import util.C
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import kotlin.concurrent.schedule

/**
 * Created by ordogod on 30.08.2019.
 **/

class PlayerThread(private val socket: Socket, val ip: String) : Thread() {

    var isReady = false
    private val reqWriter = PrintWriter(socket.getOutputStream())
    private var reconnections = 0
    private var removeTimer = makeTimer()

    override fun run() {
        while (true) {
            // makes thread live while player is connected
        }
    }

    fun resetTimer() {
        reconnections = 0
        removeTimer.cancel()
        removeTimer = makeTimer()
    }

    private fun makeTimer() = Timer("RemovePlayer", false).schedule(1000, 1000) {
        if (++reconnections < C.MAX_RECONNECTIONS) {
            Log.i("$ip not responding: $reconnections.")
        }
        else {
            C.removePlayer(this@PlayerThread)
            this.cancel()
            this@PlayerThread.interrupt()
        }
    }
}