package forestsim.units

import forestsim.Forest
import forestsim.Terminal
import org.codetome.zircon.api.color.TextColor
import org.codetome.zircon.api.color.TextColorFactory

class Coordinate(private val y: Int, private val x: Int) {
    fun around(): Coordinate {
        val xoff = Forest.rand.nextInt(3) - 1
        val yoff = Forest.rand.nextInt(3) - 1
        return Coordinate(
                (y + yoff + Forest.SIZE) % Forest.SIZE,
                (x + xoff + Forest.SIZE) % Forest.SIZE)
    }

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is Coordinate) return false
        return other.x == x && other.y == y
    }

    override fun hashCode(): Int {
        var result = y
        result = 31 * result + x
        return result
    }

    companion object {
        fun random(): Coordinate {
            return Coordinate(Forest.rand.nextInt(Forest.SIZE), Forest.rand.nextInt(Forest.SIZE))
        }
    }
}

interface IUnit {
    var location: Coordinate
    val color: TextColor
    fun draw() {
        Terminal.draw(color)
    }
    fun move(c: Coordinate) {
        location = c
    }
}

interface ITree : IUnit {
    var age: Int
}

interface IGrowableTree<out T : IHarvestableTree> : ITree {
    fun isGrown(): Boolean
    fun grow(): T
}

interface ISpawnerTree : ITree {
    fun canSpawnSapling(): Boolean
}

interface IHarvestableTree : ISpawnerTree {
    fun harvest(): Int
}

class Sapling(override var location: Coordinate) : IGrowableTree<Tree> {
    override var age = 0
    override val color = TextColorFactory.fromRGB(50, 205, 50)

    override fun isGrown(): Boolean {
        return age == 12
    }

    override fun grow(): Tree {
        return Tree(location)
    }
}

class Tree(override var location: Coordinate) : IGrowableTree<ElderTree>, IHarvestableTree {
    override var age = 0
    override val color = TextColorFactory.fromRGB(34, 139, 34)

    override fun canSpawnSapling(): Boolean {
        return Forest.rand.nextFloat() <= .1
    }

    override fun isGrown(): Boolean {
        return age == 120
    }

    override fun grow(): ElderTree {
        return ElderTree(location)
    }

    override fun harvest(): Int {
        return 1
    }
}

class ElderTree(override var location: Coordinate) : IHarvestableTree {
    override var age = 0
    override val color = TextColorFactory.fromRGB(0, 100, 0)

    override fun canSpawnSapling(): Boolean {
        return Forest.rand.nextFloat() <= .2
    }

    override fun harvest(): Int {
        return 2
    }
}

class Lumberjack(override var location: Coordinate) : IUnit {
    var lumberHarvested = 0
    override val color = TextColorFactory.fromRGB(255, 255, 255)
}

class Bear(override var location: Coordinate) : IUnit {
    var lumberjacksMawed = 0
    override val color = TextColorFactory.fromRGB(139, 0, 0)
}