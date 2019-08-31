package util

import server.PlayerThread

/**
 * Created by ordogod on 29.08.2019.
 **/

object C {
    val INFO_WAITING_PLAYERS = "Waiting for players…".toByteArray()
    val INFO_PLAYERS_CONNECTED = "All players connected! Starting game…".toByteArray()
    val INFO_NO_SLOTS_LEFT = "All players already connected, come back later.".toByteArray()

    val MAX_FAILED_CONNECTIONS = 3

    var players = ArrayList<PlayerThread>()

    fun findPlayer(ip: String): Boolean {
        val i = players.iterator()
        while (i.hasNext())
            if (i.next().ip == ip) return true
        return false
    }

    fun addPlayer(player: PlayerThread) = players.add(player)
}