fun main() {
  fun part1(input: List<String>): Int =
      input.sumOf { line ->
        val digits = line.filter { it.isDigit() }
        "${digits.first()}${digits.last()}".toInt()
      }

  fun part2(input: List<String>): Int {
    val replacements =
        mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
        )
    val digits = replacements.keys + (1..9).map { it.toString() }

    return input.sumOf { line ->
      val (_, firstOccurrence) =
          line.findAnyOf(digits) ?: throw IllegalArgumentException("Can't find first digit")
      val (_, lastOccurrence) =
          line.findLastAnyOf(digits) ?: throw IllegalArgumentException("Can't find last digit")
      val firstDigit = firstOccurrence.toIntOrNull() ?: replacements.getValue(firstOccurrence)
      val lastDigit = lastOccurrence.toIntOrNull() ?: replacements.getValue(lastOccurrence)
      "${firstDigit}${lastDigit}".toInt()
    }
  }

  val testInput1 = readInput("Day01_test1")
  check(part1(testInput1) == 142)
  val testInput2 = readInput("Day01_test2")
  check(part2(testInput2) == 281)

  val input = readInput("Day01")
  part1(input).println()
  part2(input).println()
}
