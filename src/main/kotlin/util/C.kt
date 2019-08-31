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

    val INFO_WAITING_PLAYERS = "Waiting for players…".toByteArray()
    val INFO_PLAYERS_CONNECTED = "All players connected! Starting game…".toByteArray()
    val INFO_NO_SLOTS_LEFT = "All players already connected, come back later.".toByteArray()

    val MAX_FAILED_CONNECTIONS = 3

    var players = ArrayList<PlayerThread>()

    init {
        DATE_FORMAT.timeZone = TimeZone.getTimeZone(ZoneId.of("Africa/Addis_Ababa"))
    }

    fun findPlayer(ip: String): Boolean {
        val i = players.iterator()
        while (i.hasNext())
            if (i.next().ip == ip) return true
        return false
    }

    fun addPlayer(player: PlayerThread) = players.add(player)

    fun beautyDate() = DATE_FORMAT.format(Date()).toString()
}