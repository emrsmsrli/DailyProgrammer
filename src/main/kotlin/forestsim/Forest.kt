package forestsim

import forestsim.units.*
import java.util.*

class Forest {
    private var ageInMonths = 0
    private var ageInYears = 0

    private val units = mutableListOf<IUnit>()

    fun spawn() {
        var treeAmount = 0
        var lumberjackAmount = 0
        var bearAmount = 0
        val treeRatio = SIZE / 2
        val ljRatio = SIZE / 10
        val bearRatio = SIZE / 50

        spawner@while(true) {
            var location: Coordinate
            do {
                location = Coordinate.random()
            } while(units.any { it.location == location })

            when {
                treeRatio != treeAmount -> {
                    units.add(Tree(location))
                    treeAmount++
                }
                ljRatio != lumberjackAmount -> {
                    units.add(Lumberjack(location))
                    lumberjackAmount++
                }
                bearRatio != bearAmount -> {
                    units.add(Bear(location))
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
        val trees = units.filterIsInstance<ITree>()
        val lumberjacks = units.filterIsInstance<Lumberjack>()
        val bears = units.filterIsInstance<Bear>()

        trees.forEach { tree ->
            if(tree is ISpawnerTree) {
                if(tree.canSpawnSapling()) {
                    var location: Coordinate
                    var c = 0
                    do {
                        location = tree.location.around()
                        c++
                    } while(units.any { it.location == location } && c < 8)

                    if(c != 8)
                        units.add(Sapling(location))
                }
            }
            if(tree is IGrowableTree<*>) {
                if(tree.isGrown()) {
                    units.removeIf { it == tree}
                    val grown = tree.grow()
                    units.add(grown)

                    val lj = lumberjacks.firstOrNull { it.location == grown.location }
                    if(lj != null) {
                        lj.lumberHarvested += grown.harvest()
                        units.remove(grown)
                    }
                }
            }
        }

        lumberjacks.forEach { lj ->
            for(i in 1..3) {
                var newLoc = lj.location.around()
                while(lumberjacks.any { it.location == newLoc })
                    newLoc = lj.location.around()
                lj.location = newLoc
                val t = trees.filterIsInstance<IHarvestableTree>().firstOrNull { it.location == lj.location }
                if(t != null) {
                    units.remove(t)
                    lj.lumberHarvested += t.harvest()
                    break
                }
                val b = bears.firstOrNull { it.location == lj.location }
                if(b != null) {
                    units.remove(lj)
                    if(lumberjacks.size == 2) {
                        var location: Coordinate
                        do {
                            location = Coordinate.random()
                        } while(units.any { (it is Bear || it is Lumberjack) && it.location == location })
                        units.add(Lumberjack(location))
                    }
                    b.lumberjacksMawed++
                    break
                }
            }
        }

        bears.forEach { bear ->
            for(i in 1..5) {
                var newLoc = bear.location.around()
                while(bears.any { it.location == newLoc })
                    newLoc = bear.location.around()
                bear.location = newLoc
                val lj = lumberjacks.firstOrNull { it.location == bear.location }
                if(lj != null) {
                    units.remove(lj)
                    if(lumberjacks.size == 2) {
                        var location: Coordinate
                        do {
                            location = Coordinate.random()
                        } while(units.any { (it is Bear || it is Lumberjack) && it.location == location })
                        units.add(Lumberjack(location))
                    }
                    bear.lumberjacksMawed++
                    break
                }
            }
        }

        draw()
        advanceTime()

        if(ageInMonths % 12 == 0) {
            var location: Coordinate

            val totalLumberCollected = lumberjacks.sumBy { it.lumberHarvested }
            val totalMawedLumberjacks = bears.sumBy { it.lumberjacksMawed }

            if(totalLumberCollected >= lumberjacks.size) {
                for(i in 0..(totalLumberCollected - lumberjacks.size) / 10) {
                    do {
                        location = Coordinate.random()
                    } while(units.any { (it is Bear || it is Lumberjack) && it.location == location })
                    units.add(Lumberjack(location))
                }
            } else {
                units.remove(lumberjacks[rand.nextInt(lumberjacks.size)])
            }

            if(totalMawedLumberjacks == 0) {
                do {
                    location = Coordinate.random()
                } while(units.any { (it is Bear || it is Lumberjack) && it.location == location })
                units.add(Bear(location))
            } else {
                units.remove(bears[rand.nextInt(bears.size)])
            }

            lumberjacks.forEach { it.lumberHarvested = 0 }
            bears.forEach { it.lumberjacksMawed = 0 }
        }
    }

    fun isDying(): Boolean {
        return units.filterIsInstance<ITree>().isEmpty() || ageInYears == 400
    }

    private fun draw() {
        (0 until SIZE).forEach { y ->
            (0 until SIZE).map { x ->
                val us = units.filter { it.location == Coordinate(y, x) }
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