package asciigameoflife

import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel

val neigbours = arrayOf(Pair(-1, -1), Pair(-1, 0), Pair(-1, 1), Pair(0, -1),
                        Pair(0, 1), Pair(1, -1), Pair(1, 0), Pair(1, 1))

val input = """ 7 10 10
                0 0 0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0 0 0
                0 0 1 0 0 0 0 0 0 0
                0 0 0 1 0 0 0 0 0 0
                0 1 1 1 0 0 0 0 0 0
                0 0 0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0 0 0 """

val challenge = """ 32 17 17
                    0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                    0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                    0 0 0 0 1 1 1 0 0 0 1 1 1 0 0 0 0
                    0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                    0 0 1 0 0 0 0 1 0 1 0 0 0 0 1 0 0
                    0 0 1 0 0 0 0 1 0 1 0 0 0 0 1 0 0
                    0 0 1 0 0 0 0 1 0 1 0 0 0 0 1 0 0
                    0 0 0 0 1 1 1 0 0 0 1 1 1 0 0 0 0
                    0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                    0 0 0 0 1 1 1 0 0 0 1 1 1 0 0 0 0
                    0 0 1 0 0 0 0 1 0 1 0 0 0 0 1 0 0
                    0 0 1 0 0 0 0 1 0 1 0 0 0 0 1 0 0
                    0 0 1 0 0 0 0 1 0 1 0 0 0 0 1 0 0
                    0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                    0 0 0 0 1 1 1 0 0 0 1 1 1 0 0 0 0
                    0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                    0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 """"

fun main(args: Array<String>) {
    // init
    val reader = Scanner(challenge) // Scanner(challenge || input)
    val simCount = reader.nextInt()
    val h = reader.nextInt()
    val w = reader.nextInt()
    val imageBuffer = BufferedImage(w * 50, h * 50, BufferedImage.TYPE_INT_ARGB)

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

    // print first gen
    lastGeneration.printGen()

    with(JFrame()) {
        this.setSize(w * 50, h * 50)
        this.add(object : JPanel() {
            override fun paintComponent(g: Graphics?) {
                super.paintComponent(g)
                g?.drawImage(imageBuffer, 0, 0, this)
            }
        })
        isVisible = true

        // simulate for simCount times
        for (i in 1..simCount) {
            imageBuffer.graphics.clearRect(0, 0, w * 50, h * 50)
            calculateNextGen(heigth = h, width = w, currentGen = lastGeneration, nextGen = currentGeneration)
            currentGeneration.forEachIndexed { y, ints -> ints.forEachIndexed { x, value -> lastGeneration[y][x] = value } }
            imageBuffer.graphics.color = Color.black
            imageBuffer.graphics.drawRect(0, 0, imageBuffer.width, imageBuffer.height)
            currentGeneration.forEachIndexed { y, ints -> ints.forEachIndexed { x, value ->
                if(value == 1) {
                    imageBuffer.graphics.color = Color.black
                    imageBuffer.graphics.fillRect(x * 50, (h - y - 1) * 50, 50, 50)
                } } }
            //currentGeneration.printGen()
            this.imageUpdate(imageBuffer, 32,0, 0, h * 50, w * 50)

            Thread.sleep(300)
        }
    }
}

fun calculateNextGen(heigth: Int, width: Int, currentGen: Array<IntArray>, nextGen: Array<IntArray>) {
    for (i in currentGen.indices) {
        for (j in currentGen[i].indices) {
            var aliveNeighbours = 0
            for ((nY, nX) in neigbours) {   // count the alive neighbours
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

fun Array<IntArray>.printGen() {
    fun IntArray.printArray() {
        for (i in this)
            print("${if (i == 1) "#" else "."} ")
        println()
    }

    for (line in this)
        line.printArray()
    println()
}