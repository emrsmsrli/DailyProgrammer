// https://www.reddit.com/r/dailyprogrammer/comments/3x3hqa/20151216_challenge_245_intermediate_ggggggg_gggg/

package ggggg

import java.util.*

val letterDictionary = mutableMapOf<String, String>()
val wordDictionary = mutableMapOf<String, String>()

/*val input: String = """H GgG d gGg e ggG l GGg o gGG r Ggg w ggg
GgGggGGGgGGggGG, ggggGGGggGGggGg!"""*/

val encoded = """a GgG d GggGg e GggGG g GGGgg h GGGgG i GGGGg l GGGGG m ggg o GGg p Gggg r gG y ggG
GGGgGGGgGGggGGgGggG /gG/GggGgGgGGGGGgGGGGGggGGggggGGGgGGGgggGGgGggggggGggGGgG!"""

fun main(args: Array<String>) {
    using(Scanner(encoded)) {
        println(decode(nextLine()))
    }
}

fun loadDicts(charMap: String) {
    using(Scanner(charMap)) {
        while(hasNext()) {
            letterDictionary.put(next(), next())
        }
    }
    letterDictionary.forEach { k, v -> wordDictionary.put(v, k) }
}

fun decode(encoded: String): String {
    val decoded = StringBuilder(encoded.length / 2)
    using(Scanner(encoded)) {
        useDelimiter("")

        while(hasNext()) {
            val f = next()
            if(!(f equalTo "g")) {
                decoded += f
                continue
            }
            val s = next()
            if(!(s equalTo "g")) {
                decoded += s
                continue
            }
            val t = next()
            if(!(t equalTo "g")) {
                decoded += t
                continue
            }
            decoded += wordDictionary["$f$s$t"]
        }

        return decoded.toString()
    }
}

fun encode(encoded: String): String {
    //var comb

    return with(StringBuilder(1024)) {
        encoded.forEach { this += letterDictionary[it.toString()] ?: it.toString() }
    }.toString()
}