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

    val RECONNECTIONS_MAX = 10
    val PLAYERS_MAX = 1

    var round = 1
    var rounds = 3

    var players = ArrayList<PlayerThread>()

    init { DATE_FORMAT.timeZone = TimeZone.getTimeZone(ZoneId.of("Africa/Addis_Ababa")) }

    fun findPlayer(ip: String): PlayerThread? =
        players.find { it.ip == ip }

    fun containsPlayer(ip: String): Boolean =
        findPlayer(ip) != null

    fun addPlayer(player: PlayerThread) {
        players.add(player)
        Log.i("${player.ip} added to players list: now ${players.size}.")
    }

    fun removePlayer(player: PlayerThread) {
        players.remove(player)
        Log.i("${player.ip} removed from players list: now ${players.size}.")
    }

    fun prettyDate() = DATE_FORMAT.format(Date()).toString()

    fun countReadyPlayers(): Int =
        players.filter { it.isReady }.size
}