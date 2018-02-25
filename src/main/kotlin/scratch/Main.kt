package scratch

import java.util.*
import kotlin.streams.toList



/*float smooth_step(uint8_t i, uint8_t N, uint8_t min, uint8_t max) {
    float v = i / (float) N;
    v = SMOOTH_STEP(v);
    return (min * v) + (max * (1 - v));
}*/

fun smoothStep(i: Short, N: Short, min: Short, max: Short): Float {
    print("$i $N $min $max ")
    fun ss(x: Float) : Float {
        return ((x) * (x) * (3 - 2 * (x)))
    }
    val v = ss(i / N.toFloat())
    return (min * v) + (max * (1 - v))
}

fun main(args: Array<String>) {

    for(i in 0..200) {
        println(smoothStep(i.toShort(), 200.toShort(), 255.toShort(), 0).toShort())
    }

    /*val argms = args.toMutableList().stream().map { Integer.parseInt(it) }.toList()

    val now = System.currentTimeMillis()
    for(i in 0..100) {
        var v = i / 100.toFloat()
        v = smoothStep(v)
        val x = (255 * v) + (0 * (1 - v))
        //val x = (0 * v) + (255 * (1 - v))
        println("$v $x")
        Thread.sleep(1)
    }
    print("Elapsed: ${System.currentTimeMillis() - now} ms")*/
}

fun smoothStep(x: Float): Float {
    return ((x) * (x) * (3 - 2 * (x)))
}