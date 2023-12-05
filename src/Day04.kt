import kotlin.math.pow

fun main() {
  fun part1(input: List<String>) =
      input
          .map { it.parse() }
          .map { (winning, mine) -> mine.intersect(winning) }
          .sumOf {
            when (it.size) {
              0 -> 0.0
              else -> 2.0.pow(it.size - 1)
            }
          }
          .toInt()

  fun part2(input: List<String>): Int {

    val cards = (1..input.size).associateWith { 1 }.toMutableMap()

    for ((idx, card) in input.withIndex()) {
      val (winning, mine) = card.parse()
      val intersection = mine.intersect(winning)
      val copiesRange = ((idx + 2) ..< (idx + 2 + intersection.size))
      val currentCopies = cards.getValue(idx + 1)

      copiesRange.forEach { cardIdx ->
        cards.merge(cardIdx, currentCopies) { acc, value -> acc + value }
      }
    }

    return cards.values.sum()
  }

  val testInput1 = readInput("Day04_test1")
  check(part1(testInput1) == 13)
  val testInput2 = readInput("Day04_test1")
  check(part2(testInput2) == 30)

  val input = readInput("Day04")
  part1(input).println()
  part2(input).println()
}

private fun String.parse(): Pair<List<Int>, List<Int>> {
  val (_, numbers) = split(":")
  val (winning, mine) = numbers.split("|")
  return Pair(winning.toInts(), mine.toInts())
}

private fun String.toInts(delimiter: Regex = "\\s+".toRegex()) =
    trim().split(delimiter).map { it.toInt() }
