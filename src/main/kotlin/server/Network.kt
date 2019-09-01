package server

import java.io.BufferedReader

/**
 * Created by ordogod on 02.09.2019.
 **/

object Network {

    fun decodeData(inReader: BufferedReader): String {
        val data: CharArray
        var dataLength = 0
        while (true) {
            val line = inReader.readLine().toLowerCase()
            if (line.contains("content-length")) {
                dataLength = line.split(" ")[1].toInt()
            }
            if (line == "") break
        }

        data = CharArray(dataLength)
        inReader.read(data, 0, dataLength)
        return String(data)
    }
}