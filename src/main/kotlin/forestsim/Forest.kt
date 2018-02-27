package forestsim

import forestsim.units.*
import forestsim.Events.Event
import forestsim.Events.Type
import forestsim.Events.Time
import java.util.*

class Forest {
    private var ageInMonths = 0
    private var ageInYears = 0

    private val units = mutableListOf<IUnit>()

    fun spawn() {
        val area = SIZE * SIZE
        var treeAmount = 0
        var lumberjackAmount = 0
        var bearAmount = 0
        val treeRatio = area / 2
        val ljRatio = area / 10
        val bearRatio = area / 50

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

        Events.log("START", "Forest spawned with $treeAmount trees, $lumberjackAmount lumberjacks, $bearAmount bears")
        draw()
    }

    fun tick() {
        val trees = units.filterIsInstance<ITree>()
        val lumberjacks = units.filterIsInstance<Lumberjack>()
        val bears = units.filterIsInstance<Bear>()

        var sapSpawned = 0
        var lumHarvest = 0
        var ljsMawed = 0
        var growT = 0
        var growE = 0

        for(tree in trees) {
            if(tree is ISpawnerTree) {
                if(tree.canSpawnSapling()) {
                    var location: Coordinate
                    var c = 0
                    do {
                        location = tree.location.around()
                        c++
                    } while(units.any { it.location == location } && c < 8)

                    if(c != 8) {
                        units.add(Sapling(location))
                        sapSpawned++
                    }
                }
            }
            if(tree is IGrowableTree<*>) {
                if(tree.isGrown()) {
                    units.removeIf { it == tree}
                    val grown = tree.grow()
                    units.add(grown)

                    if(grown is ElderTree) growE++ else growT++

                    val lj = lumberjacks.firstOrNull { it.location == grown.location }
                    if(lj != null) {
                        lj.lumberHarvested += grown.harvest()
                        lumHarvest += grown.harvest()
                        units.remove(grown)
                    }
                }
            }
        }

        for(lj in lumberjacks) {
            for(i in 1..3) {
                var newLoc = lj.location.around()
                while(lumberjacks.any { it.location == newLoc })
                    newLoc = lj.location.around()
                lj.location = newLoc
                val t = trees.filterIsInstance<IHarvestableTree>().firstOrNull { it.location == lj.location }
                if(t != null) {
                    units.remove(t)
                    lj.lumberHarvested += t.harvest()
                    lumHarvest += t.harvest()
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
                    ljsMawed++
                    break
                }
            }
        }

        for(bear in bears) {
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
                    ljsMawed++
                    break
                }
            }
        }

        draw()
        advanceTime()

        // EVENTS
        Events.addEvent(Event(Type.SPAWN_SAPLING, Time(ageInMonths), sapSpawned))
        Events.addEvent(Event(Type.HARVEST, Time(ageInMonths), lumHarvest))
        Events.addEvent(Event(Type.MAW, Time(ageInMonths), ljsMawed))
        Events.addEvent(Event(Type.GROW_E, Time(ageInMonths), growE))
        Events.addEvent(Event(Type.GROW_T, Time(ageInMonths), growT))

        if(ageInMonths % 12 == 0) {
            var location: Coordinate

            val totalLumberCollected = lumberjacks.sumBy { it.lumberHarvested }
            val totalMawedLumberjacks = bears.sumBy { it.lumberjacksMawed }
            var hire = 0
            var fire = 0
            var capture = 0
            var birth = 0

            if(totalLumberCollected >= lumberjacks.size) {
                for(i in 0..(totalLumberCollected - lumberjacks.size) / 10) {
                    do {
                        location = Coordinate.random()
                    } while(units.any { (it is Bear || it is Lumberjack) && it.location == location })
                    units.add(Lumberjack(location))
                    hire++
                }
            } else {
                units.remove(lumberjacks[rand.nextInt(lumberjacks.size)])
                fire++
            }

            if(totalMawedLumberjacks == 0) {
                do {
                    location = Coordinate.random()
                } while(units.any { (it is Bear || it is Lumberjack) && it.location == location })
                units.add(Bear(location))
                capture++
            } else {
                units.remove(bears[rand.nextInt(bears.size)])
                birth++
            }

            Events.addEvent(Event(Type.HARVEST, Time(ageInYears, false), totalLumberCollected))
            Events.addEvent(Event(Type.MAW, Time(ageInYears, false), totalMawedLumberjacks))
            Events.addEvent(Event(Type.HIRE, Time(ageInYears, false), hire))
            Events.addEvent(Event(Type.FIRE, Time(ageInYears, false), fire))
            Events.addEvent(Event(Type.BEAR_BORN, Time(ageInYears, false), capture))
            Events.addEvent(Event(Type.BEAR_CAPTURED, Time(ageInYears, false), birth))

            lumberjacks.forEach { it.lumberHarvested = 0 }
            bears.forEach { it.lumberjacksMawed = 0 }
        }

        Events.log()
    }

    fun isDying(): Boolean {
        return units.filterIsInstance<ITree>().isEmpty() || ageInYears == 400
    }

    fun die() {
        val reason = if(ageInYears == 400) "old age"
        else "human greed"
        Events.log("END", "Forest died because of $reason")
        Events.close()
        Terminal.close()
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