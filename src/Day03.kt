import kotlin.math.max
import kotlin.math.min

fun main() {

  fun part1(input: List<String>): Int {
    var sum = 0
    for ((lineNumber, range) in input.findNumberRanges()) {
      val current = input[lineNumber]
      val previous = input.getOrElse(lineNumber - 1) { dots(current.length) }
      val next = input.getOrElse(lineNumber + 1) { dots(current.length) }

      val rangeExt = range.extend(current.length)
      val foundSymbol =
          (previous.slice(rangeExt) + current.slice(rangeExt) + next.slice(rangeExt))
              .toCharArray()
              .any { it.isSymbol() }

      if (foundSymbol) {
        val number = current.slice(range)
        sum += number.toInt()
      }
    }
    return sum
  }

  fun part2(input: List<String>): Int {
    var sum = 0
    for ((lineNumber, idx) in input.findStars()) {
      val current = input[lineNumber]
      val previous = input.getOrElse(lineNumber - 1) { dots(current.length) }
      val next = input.getOrElse(lineNumber + 1) { dots(current.length) }

      val adjacentNumbers =
          current.findAdjacentNumbers(idx) +
              previous.findAdjacentNumbers(idx) +
              next.findAdjacentNumbers(idx)
      if (adjacentNumbers.size == 2) {
        val (first, second) = adjacentNumbers
        sum += first * second
      }
    }
    return sum
  }

  val testInput1 = readInput("Day03_test1")
  check(part1(testInput1) == 4361)
  val testInput2 = readInput("Day03_test1")
  check(part2(testInput2) == 467835)

  val input = readInput("Day03")
  part1(input).println()
  part2(input).println()
}

private fun List<String>.findNumberRanges() = sequence {
  for ((number, line) in withIndex()) {
    line.findNumberRanges().forEach { yield(number to it) }
  }
}

private fun String.findNumberRanges() = sequence {
  var startIdx = -1

  for ((idx, c) in withIndex()) {
    when {
      c.isDigit() && startIdx == -1 -> {
        startIdx = idx
      }
      !c.isDigit() && startIdx != -1 -> {
        yield(startIdx ..< idx)
        startIdx = -1
      }
      c.isDigit() && idx == length - 1 -> {
        yield(startIdx..idx)
      }
    }
  }
}

private fun List<String>.findStars() = sequence {
  for ((lineNumber, line) in withIndex()) {
    for ((idx, c) in line.toCharArray().withIndex()) {
      if (c == '*') {
        yield(lineNumber to idx)
      }
    }
  }
}

private fun String.findAdjacentNumbers(initialIdx: Int): List<Int> {
  val range = (initialIdx..initialIdx).extend(length - 1)
  return findNumberRanges()
      .filter { it.intersect(range) }
      .map { slice(it) }
      .map { it.toInt() }
      .toList()
}

private fun IntRange.intersect(range: IntRange) = first <= range.last && range.first <= last

private fun Char.isSymbol() = !isDigit() && this != '.'

private fun IntRange.extend(max: Int) = max(first - 1, 0)..min(last + 1, max - 1)

fun dots(length: Int) = (0..length).map { '.' }.joinToString(separator = "")
