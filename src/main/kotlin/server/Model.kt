package server

import util.C
import java.util.*

/**
 * Created by ordogod on 07.09.2019.
 **/

object Model {

    fun onReceive(data: String, fromIP: String) {
        when (data) {
            "isConnected=true" -> findPlayer(fromIP)?.resetTimer()
            "thisReset"        -> findPlayer(fromIP)?.resetData()
            "isReady=true"     -> findPlayer(fromIP)?.isReady = true
            "isReady=false"    -> findPlayer(fromIP)?.isReady = false
            "thisTurn=true"    -> findPlayer(fromIP)?.isTurn = true
            "thisTurn=false"   -> findPlayer(fromIP)?.isTurn = false
            "turnReverse"      -> reverseTurn(fromIP)
        }
        with (data) {
            when {
                this.contains("rounds=")       ->
                    C.rounds = data.split("=")[1].toInt()
                this.contains("thisDistLose=") ->
                    findPlayer(fromIP)?.lossDist = data.split("=")[1].toInt()
                this.contains("thatDistLose=") ->
                    C.players.find { it.ip != fromIP }?.lossDist = data.split("=")[1].toInt()
            }
        }
    }

    fun findPlayer(ip: String): PlayerThread? =
        C.players.find { it.ip == ip }

    fun containsPlayer(ip: String): Boolean =
        C.players.find { it.ip == ip } != null

    fun addPlayer(player: PlayerThread) {
        C.players.add(player)
        Log.i("${player.ip} added to players list: now ${C.players.size}.")
    }

    fun removePlayer(player: PlayerThread) {
        C.players.remove(player)
        Log.i("${player.ip} removed from players list: now ${C.players.size}.")
    }

    fun prettyDate() = C.DATE_FORMAT.format(Date()).toString()

    private fun reverseTurn(ip: String) {
        val thisPlayer = C.players.find { it.ip == ip }!!
        val thatPlayer = C.players.find { it.ip != ip }!!
        thisPlayer.isTurn = !thisPlayer.isTurn
        thatPlayer.isTurn = !thatPlayer.isTurn
        println("Turns reversed, now ${C.players[0].isTurn} and ${C.players[1].isTurn}.")
    }
}