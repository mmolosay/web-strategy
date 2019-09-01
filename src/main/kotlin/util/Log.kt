package util

/**
 * Created by ordogod on 27.08.2019.
 **/

object Log {

    private const val TAG_INFO = "INFO"
    private const val TAG_SUCCESS = "SUCCESS"
    private const val TAG_FAIL = "FAIL"
    private const val TAG_CUSTOM = "CUSTOM"
    private const val TAG_DEBUG = "DEBUG"

    const val ANSI_RESET = "\u001B[0m"
    const val ANSI_BLACK = "\u001B[30m"
    const val ANSI_RED = "\u001B[31m"
    const val ANSI_GREEN = "\u001B[32m"
    const val ANSI_YELLOW = "\u001B[33m"
    const val ANSI_BLUE = "\u001B[34m"
    const val ANSI_PURPLE = "\u001B[35m"
    const val ANSI_CYAN = "\u001B[36m"
    const val ANSI_WHITE = "\u001B[37m"

    const val ANSI_BLACK_BACKGROUND = "\u001B[40m"
    const val ANSI_RED_BACKGROUND = "\u001B[41m"
    const val ANSI_GREEN_BACKGROUND = "\u001B[42m"
    const val ANSI_YELLOW_BACKGROUND = "\u001B[43m"
    const val ANSI_BLUE_BACKGROUND = "\u001B[44m"
    const val ANSI_PURPLE_BACKGROUND = "\u001B[45m"
    const val ANSI_CYAN_BACKGROUND = "\u001B[46m"
    const val ANSI_WHITE_BACKGROUND = "\u001B[47m"

    // INFO
    fun i(msg: String, ansiColor: String = ANSI_RESET) =
        println("$ansiColor[$TAG_INFO]: ${C.beautyDate()}: $msg $ANSI_RESET")

    // SUCCESS
    fun s(msg: String, ansiColor: String = ANSI_GREEN) =
        println("$ansiColor[$TAG_SUCCESS]: $msg $ANSI_RESET")

    // FAIL
    fun f(msg: String, ansiColor: String = ANSI_RED) =
        println("$ansiColor[$TAG_FAIL]: $msg $ANSI_RESET")

    // DEBUG
    fun d(msg: String, ansiColor: String = ANSI_YELLOW) =
        println("$ansiColor[$TAG_DEBUG]: $msg $ANSI_RESET")

    // CUSTOM
    fun c(msg: String, ansiColor: String = ANSI_WHITE, tag: String = TAG_CUSTOM) =
        println("$ansiColor[$tag]: $msg $ANSI_RESET")
}