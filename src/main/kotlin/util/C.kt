package util

import server.PlayerThread
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ordogod on 29.08.2019.
 **/

object C {
    private val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy H:m:s")

    val MAX_FAILED_CONNECTIONS = 10
    val MAX_PLAYERS = 2

    val RES_WAITING_PLAYERS = "Waiting for players…".toByteArray()
    val RES_PLAYERS_CONNECTED = "All players connected! Starting game…".toByteArray()
    val RES_NO_SLOTS_LEFT = ("<body style=\"display: flex; flex-direction: row; justify-content: center;" +
                             " align-items: center;\"><img src=nardy.jpg></body>").toByteArray()



    var players = ArrayList<PlayerThread>()

    init {
        DATE_FORMAT.timeZone = TimeZone.getTimeZone(ZoneId.of("Africa/Addis_Ababa"))
    }

    fun hasPlayer(ip: String): Boolean {
        for (player in players) {
            if (player.ip == ip) return true
        }
        return false
    }

    fun findPlayer(ip: String): PlayerThread? {
        for (player in players) {
            if (player.ip == ip) return player
        }
        return null
    }

    fun addPlayer(player: PlayerThread) {
        players.add(player)
        Log.i("${player.ip} added to players list: now ${players.size}.")
    }

    fun removePlayer(player: PlayerThread) {
        players.remove(player)
        Log.i("${player.ip} removed from players list: now ${players.size}.")
    }

    fun beautyDate() = DATE_FORMAT.format(Date()).toString()
}