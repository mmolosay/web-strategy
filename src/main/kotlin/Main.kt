import server.MainServer
import kotlin.system.exitProcess

/**
 * Created by ordogod on 28.08.2019.
 **/

fun main(args: Array<String>) {

    val argsList = arrayOf(
        Pair("-s", "--server"),
        Pair("-p", "--port"),
        Pair("-h", "--help"),
        Pair("-d", "--date")
    )

    Log.c("Welcome ^_^", Log.ANSI_CYAN)
    argsApply(argsValues(argsList, args))
}

fun argsValues(argsList: Array<Pair<String, String>>, args: Array<String>): ArrayList<Pair<String, String>> {
    val values = arrayListOf<Pair<String, String>>()
    for (arg in args)
        for (argPair in argsList)
            if (arg.contains(argPair.first) || arg.contains(argPair.second)) {
                val value =
                    if (arg.split(":").size == 2) arg.split(":")[1].toLowerCase()
                    else "0"
                values.add(Pair(argPair.first, value))
            }
    return values
}

fun argsApply(argsValued: ArrayList<Pair<String, String>>) {
    if (argsValued.isEmpty()) Log.e(
        "No arguments were passed. " +
        "Use \'-h\' or \'--help\' to see list of arguments."
    )
    if (hasArg("-h", argsValued)) showHelp()

    val host = (argsValued[0].first == "-s")
    try {
        for (argValued in argsValued)
            when (argValued.first) {
                "-p" -> {
                    if (host) MainServer.port = argValued.second.toInt()
                    else Log.e("You must specify a server start if you specify the port.")
                }
                "-d" -> {
                    if (host) Log.withDate = argValued.second.toBoolean()
                    else Log.e("You must specify a server start if you specify the dating of logs.")
                }
            }
    } catch (e: Exception) { e.printStackTrace() }

    if (host) MainServer.start()
}

fun showHelp() {
    println("HELP\n")

    println("-h,        --help             Shows this help message.")
    println("-s,        --server           Starts server at specified port (or at 8080 by default).")
    println("-p:<int>,  --port:<int>       Sets given port to server.")
    println("-d:<bool>, --date:<bool>      Logs in console with date/time prefix.")
    println()

    exitProcess(0)
}

fun hasArg(argName: String, args:ArrayList<Pair<String, String>>): Boolean {
    for (arg in args)
        if (arg.first == argName || arg.second == argName) return true
    return false
}