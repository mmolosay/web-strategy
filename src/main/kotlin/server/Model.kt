package server

import util.C

/**
 * Created by ordogod on 07.09.2019.
 **/

object Model {

    fun onReceive(data: String, fromIP: String) {
        when (data) {
            "isConnected=true" -> C.findPlayer(fromIP)?.resetTimer()
            "isReady=true"     -> C.findPlayer(fromIP)?.isReady = true
            "isReady=false"    -> C.findPlayer(fromIP)?.isReady = false
        }
        with (data) {
            when {
                this.contains("rounds=") -> C.rounds = data.split("=")[1].toInt()
            }
        }
    }

}