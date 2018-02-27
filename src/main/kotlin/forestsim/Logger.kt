package forestsim

import java.io.PrintWriter

class Logger(name: String) {
    private val writer = PrintWriter("$name.txt")

    fun log(type: String, msg: String) {
        writer.println("$type: $msg")
    }

    fun close() {
        writer.close()
    }
}