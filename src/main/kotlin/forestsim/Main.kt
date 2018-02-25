// https://www.reddit.com/r/dailyprogrammer/comments/27h53e/662014_challenge_165_hard_simulated_ecology_the/

package forestsim

fun main(args: Array<String>) {
    val forest = Forest()
    forest.spawn()

    while(!forest.isDying())
        forest.tick()
}