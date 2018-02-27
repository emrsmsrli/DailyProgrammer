package forestsim

object Events {
    private var totalSpawn = 0
    private var totalGrowT = 0
    private var totalGrowE = 0
    private var totalHarvest = 0
    private var totalMaw = 0
    private var totalHire = 0
    private var totalFire = 0
    private var totalBearBorn = 0
    private var totalBearCaptured = 0

    enum class Type {
        SPAWN_SAPLING,
        GROW_T,
        GROW_E,
        HARVEST,
        MAW,
        HIRE,
        FIRE,
        BEAR_BORN,
        BEAR_CAPTURED
    }

    data class Time(private val t: Int, private val isMonthly: Boolean = true) {
        fun get(): String {
            return if(isMonthly) month(t) else year(t)
        }
    }
    data class Event(val type: Type, val time: Time, val amount: Int) {
        fun valid(): Boolean {
            return amount != 0
        }
    }
    private val events = mutableListOf<Event>()

    private val l = Logger("forestsim")

    fun log() {
        events.filter { it.valid() }
                .forEach {
                    log(it.time.get(), when(it.type) {
                        Type.SPAWN_SAPLING -> {
                            totalSpawn++
                            "${it.amount} saplings spawned"
                        }
                        Type.GROW_T -> {
                            totalGrowT++
                            "${it.amount} saplings grown to trees"
                        }
                        Type.GROW_E -> {
                            totalGrowE++
                            "${it.amount} trees grown to elder trees"
                        }
                        Type.HARVEST -> {
                            totalHarvest++
                            "${it.amount} lumbers harvested"
                        }
                        Type.MAW -> {
                            totalMaw++
                            "${it.amount} lumberjacks mawed by bears"
                        }
                        Type.HIRE -> {
                            totalHire++
                            "${it.amount} lumberjacks hired"
                        }
                        Type.FIRE -> {
                            totalFire++
                            "${it.amount} lumberjacks fired"
                        }
                        Type.BEAR_BORN -> {
                            totalBearBorn++
                            "${it.amount} bears born"
                        }
                        Type.BEAR_CAPTURED -> {
                            totalBearCaptured++
                            "${it.amount} bears captured"
                        }
                    })
                }
        events.clear()
    }

    fun log(type: String, msg: String) {
        l.log(type, msg)
    }

    fun addEvent(e: Event) {
        events.add(e)
    }

    fun close() {
        l.log("TOTAL", "$totalSpawn saplings spawn")
        l.log("TOTAL", "$totalGrowT saplings grown to trees")
        l.log("TOTAL", "$totalGrowE trees grown to elder trees")
        l.log("TOTAL", "$totalHarvest lumbers harvested")
        l.log("TOTAL", "$totalMaw lumberjacks mawed")
        l.log("TOTAL", "$totalHire lumberjacks hired")
        l.log("TOTAL", "$totalFire lumberjacks fired")
        l.log("TOTAL", "$totalBearBorn bears born")
        l.log("TOTAL", "$totalBearCaptured bears captured")
        l.close()
    }
}

private fun month(month: Int): String {
    return "Month [$month]"
}

private fun year(year: Int): String {
    return "Year [$year]"
}