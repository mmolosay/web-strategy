import server.MainServer
import server.MainServer.DEFAULT_PORT
import util.Log
import java.lang.NumberFormatException
import kotlin.system.exitProcess

/**
 * Created by ordogod on 28.08.2019.
 **/

fun main(args: Array<String>) {
    Log.c("Welcome ^_^", Log.ANSI_CYAN)

    if (args.size == 1 && (args[0] == "-h" || args[0] == "--help")) printHelp()

    val argsValues = argumentsValues(arrayOf(
        Pair("-s", "--server")
    ), args)

    val port: Int =
        try { argsValues[0]?.toInt() ?: DEFAULT_PORT }
        catch (e: NumberFormatException) { DEFAULT_PORT }

    MainServer.init(port)

    Thread(MainServer).start()
}

fun argumentsValues(arguments: Array<Pair<String, String>>, args: Array<String>): Array<String?> {
    val values = arrayOfNulls<String?>(arguments.size)
    var count = 0
    for (arg in args) {
        for (argument in arguments) {
            if (arg.contains(argument.first) || arg.contains(argument.second))
                values[count++] = arg.split(":")[1]
        }
    }
    return values
}

fun printHelp() {

    println("HELP\n")
    println("-h, --help                    Shows this help message.")
    println("-s:<port>, --server:<port>    Start server at given port.")
    println()

    exitProcess(0)
}