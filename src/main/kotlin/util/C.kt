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

    val MAX_RECONNECTIONS = 10
    val MAX_PLAYERS = 2

    var round = 1
    var rounds = 3

    var players = ArrayList<PlayerThread>()

    init {
        DATE_FORMAT.timeZone = TimeZone.getTimeZone(ZoneId.of("Africa/Addis_Ababa"))
    }

    fun containsPlayer(ip: String): Boolean {
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

    fun prettyDate() = DATE_FORMAT.format(Date()).toString()

    fun playersReady(): Int {
        var count = 0
        for (player in players) if (player.isReady) count++
        return count
    }
}