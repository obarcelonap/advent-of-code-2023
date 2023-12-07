fun main() {

  fun part1(input: List<String>): Int {
    val races = input.parse()

    return races.map { countWaysToWin(it) }.reduce { acc, t -> acc * t }
  }

  fun part2(input: List<String>): Int {
    val races = input.parse()
    val raceTime = races.map { it.first }.joinToLong()
    val recordDistance = races.map { it.second }.joinToLong()

    return countWaysToWin(Pair(raceTime, recordDistance))
  }

  val testInput1 = readInput("Day06_test1")
  check(part1(testInput1) == 288)
  check(part2(testInput1) == 71503)

  val input = readInput("Day06")
  part1(input).println()
  part2(input).println()
}

private fun List<String>.parse(): List<Pair<Long, Long>> {
  val (timeLine, distanceLine) = this
  val (_, times) = timeLine.split(":")
  val (_, distances) = distanceLine.split(":")

  return times.toLongs().zip(distances.toLongs())
}

private fun countWaysToWin(race: Pair<Long, Long>): Int {
  val (raceTime, recordDistance) = race
  return (0..raceTime)
      .map { hold ->
        val timeTravelling = raceTime - hold
        hold * timeTravelling
      }
      .count { it > recordDistance }
}
