import kotlin.math.max

fun main() {

  fun part1(input: List<String>): Int {
    val bag =
        mapOf(
            "red" to 12,
            "green" to 13,
            "blue" to 14,
        )

    val quantitiesInBag = { cubes: Set<Cube> ->
      cubes.all { bag.getValue(it.color) >= it.quantity }
    }

    return input.map(Game::parse).filter { it.sets.all(quantitiesInBag) }.sumOf { it.id }
  }

  fun part2(input: List<String>): Int {
    return input.map(Game::parse).sumOf {
      val max =
          it.sets.flatten().fold(mutableMapOf("red" to 0, "green" to 0, "blue" to 0)) { acc, cube ->
            acc[cube.color] = max(acc.getValue(cube.color), cube.quantity)
            acc
          }

      max.values.reduce { a, b -> a * b }
    }
  }

  val testInput1 = readInput("Day02_test1")
  check(part1(testInput1) == 8)
  val testInput2 = readInput("Day02_test2")
  check(part2(testInput2) == 2286)

  val input = readInput("Day02")
  part1(input).println()
  part2(input).println()
}

private data class Cube(val quantity: Int, val color: String) {
  companion object {
    fun parseOne(input: String): Cube {
      val (quantity, color) = input.trim().split(" ")
      return Cube(quantity.toInt(), color)
    }

    fun parseMany(input: String) = input.split(",").map(Cube::parseOne).toSet()
  }
}

private data class Game(val id: Int, val sets: Set<Set<Cube>>) {
  companion object {
    fun parse(input: String): Game {
      val (idStr, subsetsStr) = input.split(":")
      val id = idStr.split(" ")[1].toInt()
      val subsets = subsetsStr.split(";").map(Cube::parseMany).toSet()
      return Game(id, subsets)
    }
  }
}
