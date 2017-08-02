package asciigameoflife

import java.awt.image.BufferedImage

fun BufferedImage.clear() {
    graphics.clearRect(0, 0, width, height)
}

fun Array<IntArray>.copy(to: Array<IntArray>) {
    forEachIndexed { y, ints -> ints.forEachIndexed { x, value -> to[y][x] = value } }
}