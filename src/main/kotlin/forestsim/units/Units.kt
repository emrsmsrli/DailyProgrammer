package forestsim.units

import clamp
import forestsim.Forest
import forestsim.Terminal
import org.codetome.zircon.api.color.TextColor
import org.codetome.zircon.api.color.TextColorFactory

data class Coordinate(private val y: Int, private val x: Int) {
    fun around(): Coordinate {
        val xoff = Forest.rand.nextInt(3) - 1
        val yoff = Forest.rand.nextInt(3) - 1
        return Coordinate(
                clamp(0, y + yoff, Forest.SIZE),
                clamp(0, x + xoff, Forest.SIZE))
    }

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is Coordinate) return false
        return other.x == x && other.y == y
    }
}

interface IUnit {
    var coordinate: Coordinate
    val color: TextColor
    fun draw() {
        Terminal.draw(color)
    }
    fun move(c: Coordinate) {
        coordinate = c
    }
}

interface ITree : IUnit {
    var age: Int
}

interface IGrowableTree : ITree {
    fun isGrown(): Boolean
}

interface ISpawnerTree

class Sapling(override var coordinate: Coordinate) : IGrowableTree {
    override var age = 0
    override val color = TextColorFactory.fromRGB(50, 205, 50)

    override fun isGrown(): Boolean {
        return age == 12
    }
}

class Tree(override var coordinate: Coordinate) : IGrowableTree, ISpawnerTree {
    override var age = 0
    override val color = TextColorFactory.fromRGB(34, 139, 34)

    override fun isGrown(): Boolean {
        return age == 120
    }
}

class ElderTree(override var coordinate: Coordinate) : ITree, ISpawnerTree {
    override var age = 0
    override val color = TextColorFactory.fromRGB(0, 100, 0)
}

class Lumberjack(override var coordinate: Coordinate) : IUnit {
    override val color = TextColorFactory.fromRGB(255, 255, 255)
}

class Bear(override var coordinate: Coordinate) : IUnit {
    override val color = TextColorFactory.fromRGB(178, 34, 34)
}