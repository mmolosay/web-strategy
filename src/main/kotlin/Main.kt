import util.Log
import java.lang.NumberFormatException
import kotlin.system.exitProcess

/**
 * Created by ordogod on 28.08.2019.
 **/

fun main(args: Array<String>) {
    Log.c("Welcome ^_^", Log.ANSI_CYAN)

    if (args.size == 1 && args[0] == "-help") printHelp()

    val argsValues = argumentsValues(arrayOf("-server"), args)
    val port: Int =
        try { argsValues[0]?.toInt() ?: MainServer.DEFAULT_PORT }
        catch (e: NumberFormatException) { MainServer.DEFAULT_PORT }

    MainServer.init(port)

    Thread(MainServer).start()
}

fun argumentsValues(arguments: Array<String>, args: Array<String>): Array<String?> {
    val values = arrayOfNulls<String?>(arguments.size)
    var count = 0
    for (arg in args) {
        for (argument in arguments) {
            if (arg.contains(argument)) values[count++] = arg.split(":")[1]
        }
    }
    return values
}

fun printHelp() {

    //TODO: implement help listing

    exitProcess(0)
}