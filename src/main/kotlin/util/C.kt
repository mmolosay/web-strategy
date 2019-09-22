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
    val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy H:m:s")

    val RECONNECTIONS_MAX = 10
    val PLAYERS_MAX = 2
    val PLAYERS_READY
        get() = players.filter { it.isReady }.size
    val LOSS_DIST = 4

    var players = ArrayList<PlayerThread>()

    var round = 1
    var rounds = 3


    init { DATE_FORMAT.timeZone = TimeZone.getTimeZone(ZoneId.of("Africa/Addis_Ababa")) }
}