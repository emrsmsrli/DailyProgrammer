package forestsim

import forestsim.units.*
import java.util.*

class Forest {
    var ageInMonths = 0
        private set
    var ageInYears = 0
        private set

    private var treeAmount = 0
    private var lumberjackAmount = 0
    private var bearAmount = 0

    private val units = mutableListOf<IUnit>()

    fun spawn() {
        val treeRatio = SIZE / 2
        val ljRatio = SIZE / 10
        val bearRatio = SIZE / 50

        spawner@while(true) {
            var coord: Coordinate
            do {
                coord = Coordinate(rand.nextInt(SIZE), rand.nextInt(SIZE))
            } while(units.any { it.coordinate == coord })

            when {
                treeRatio != treeAmount -> {
                    units.add(Tree(coord))
                    treeAmount++
                }
                ljRatio != lumberjackAmount -> {
                    units.add(Lumberjack(coord))
                    lumberjackAmount++
                }
                bearRatio != bearAmount -> {
                    units.add(Bear(coord))
                    bearAmount++
                }
                else -> {
                    break@spawner
                }
            }
        }

        draw()
    }

    fun tick() {
        /*for((c, us) in units.entries) {
            us.forEach { _, u ->
                when(u) {
                    is ITree -> {
                        if(u is ISpawnerTree) {
                            var coordinate: Coordinate
                            do {
                                coordinate = c.around()
                            } while(units[coordinate]?.has<ITree>())

                            units.add(Tree(coordinate))
                        }
                        if(u is IGrowableTree) {
                            if(u.isGrown())

                        }
                    }
                    is Lumberjack -> {

                    }
                    is Bear -> {

                    }
                }
            }
        }*/

        draw()
        advanceTime()
    }

    private fun draw() {
        (0 until SIZE).forEach { y ->
            (0 until SIZE).map { x ->
                val us = units.filter { it.coordinate == Coordinate(y, x) }
                when {
                    us.isEmpty() -> Terminal.draw()
                    us.size == 1 -> us[0].draw()
                    else -> Terminal.draw(us[0].color, us[1].color)
                }
            }
        }
        Terminal.flush()
    }

    private fun advanceTime() {
        ageInMonths++
        if(ageInMonths % 12 == 0)
            ageInYears++

        units.filterIsInstance<ITree>().forEach { it.age++ }
    }

    companion object {
        const val SIZE = 60
        val rand = Random(Date().time)
    }
}