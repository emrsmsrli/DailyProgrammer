package ggggg

inline fun <T : AutoCloseable, R> using(receiver: T, block: T.() -> R): R = receiver.use(block)

infix fun String.equalTo(other: String): Boolean {
    return toLowerCase() == other
}

operator fun StringBuilder.plusAssign(str: String?) {
    append(str)
}

/*fun combinations(length: Int): Int {
    return Math.(2, length) as Int
}*/