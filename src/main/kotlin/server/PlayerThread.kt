package server

import util.C
import java.net.Socket
import java.util.*
import kotlin.concurrent.schedule

/**
 * Created by ordogod on 30.08.2019.
 **/

class PlayerThread(private val socket: Socket, val ip: String) : Thread() {

    var isReady = false
    var isTurn = false
    var lossDist = C.LOSS_DIST
    private var reconnections = 0
    private var removeTimer = makeTimer()

    override fun run() {
        while (true) {
            // makes thread live while player is connected
        }
    }

    fun resetData() {
        isReady = false
        isTurn = false
        lossDist = C.LOSS_DIST
    }

    fun resetTimer() {
        reconnections = 0
        removeTimer.cancel()
        removeTimer = makeTimer()
    }

    private fun makeTimer() = Timer("RemovePlayer", false).schedule(1000, 1000) {
        if (++reconnections < C.RECONNECTIONS_MAX) {
            Log.i("$ip not responding: $reconnections.")
        }
        else {
            Model.removePlayer(this@PlayerThread)
            this.cancel()
            this@PlayerThread.interrupt()
        }
    }
}