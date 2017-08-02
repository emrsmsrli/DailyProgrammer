package asciigameoflife

import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel

val neighbours = arrayOf(Pair(-1, -1), Pair(-1, 0), Pair(-1, 1), Pair(0, -1),
                        Pair(0, 1), Pair(1, -1), Pair(1, 0), Pair(1, 1))

/*val input = """7 10 10
..........
..........
..#.......
...#......
.###......
..........
..........
..........
..........
.........."""*/

val input = """32 17 17
.................
.................
....###...###....
.................
..#....#.#....#..
..#....#.#....#..
..#....#.#....#..
....###...###....
.................
....###...###....
..#....#.#....#..
..#....#.#....#..
..#....#.#....#..
.................
....###...###....
.................
................."""

const val TILE = 50

fun main(args: Array<String>) {
    // init
    val firstGen = input.replace(".", "0 ").replace("#", "1 ")
    val reader = Scanner(firstGen)
    val simCount = reader.nextInt()
    val h = reader.nextInt()
    val w = reader.nextInt()
    val image = BufferedImage(w * TILE, h * TILE, BufferedImage.TYPE_INT_ARGB)

    val lastGeneration = Array(size = h) {
        IntArray(size = w) {
            reader.nextInt()
        }
    }

    val currentGeneration = Array(size = h) {
        val y = it
        IntArray(size = w) {
            lastGeneration[y][it]
        }
    }

    with(JFrame()) {
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.add(object : JPanel() {
            override fun paintComponent(g: Graphics?) {
                super.paintComponent(g)
                g?.drawImage(image, 0, 0, this)
            }
        })
        isVisible = true
        this.setSize(image.width, image.height + height)

        // print first gen
        draw(generation = lastGeneration, to = image, tileSize = TILE)

        // simulate for simCount times
        for (i in 1..simCount) {
            calculateNextGen(heigth = h, width = w, currentGen = lastGeneration, nextGen = currentGeneration)
            currentGeneration.copy(to = lastGeneration)

            draw(generation = currentGeneration, to = image, tileSize = TILE)
            imageUpdate(image, ImageObserver.ALLBITS,0, 0, image.width, image.height)

            Thread.sleep(300)
        }
    }
}

fun draw(generation: Array<IntArray>, to: BufferedImage, tileSize: Int) {
    val h = generation.size
    val w = generation[0].size
    to.clear()
    to.graphics.color = Color.black
    generation.forEachIndexed {
        y, ints -> (0 until w)
            .filter { ints[it] == 0 }
            .forEach { x -> to.graphics.fillRect(x * tileSize, (h - y - 1) * tileSize, tileSize, tileSize) }
    }
}

fun calculateNextGen(heigth: Int, width: Int, currentGen: Array<IntArray>, nextGen: Array<IntArray>) {
    for (i in 0 until heigth) {
        for (j in 0 until width) {
            var aliveNeighbours = 0
            for ((nY, nX) in neighbours) {   // count the alive neighbours
                val y = (i + nY + heigth) % heigth  // wrap around height
                val x = (j + nX + width) % width    // wrap around width
                if (currentGen[y][x] == 1)
                    aliveNeighbours++
            }

            if (currentGen[i][j] == 0 && aliveNeighbours == 3)
                nextGen[i][j] = 1
            else if (currentGen[i][j] == 1 && aliveNeighbours < 2 || aliveNeighbours > 3)
                nextGen[i][j] = 0
        }
    }
}