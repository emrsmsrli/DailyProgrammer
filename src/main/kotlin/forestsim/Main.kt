package forestsim

fun main(args: Array<String>) {
    val forest = Forest()
    forest.spawn()

    while(!forest.isDying())
        forest.tick()
}