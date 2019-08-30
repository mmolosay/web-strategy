package util

/**
 * Created by ordogod on 29.08.2019.
 **/

object C {
    val INFO_WAITING_PLAYERS = "Waiting for players…".toByteArray()
    val INFO_PLAYERS_CONNECTED = "All players connected! Starting game…".toByteArray()
    val INFO_NO_SLOTS_LEFT = "All players already connected, come back later.".toByteArray()

    var clientsConnected = 0
    var clientsIPs = ArrayList<String>()

    fun findIP(ip: String): Boolean {
        val i = clientsIPs.iterator()
        while (i.hasNext())
            if (i.next() == ip) return true
        return false
    }

    fun addIP(ip: String) {
        clientsIPs.add(ip)
        clientsConnected++
    }
}