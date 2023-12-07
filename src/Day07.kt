fun main() {
  fun part1(input: List<String>): Int {
    val hands = input.parse()

    return hands
        .sortedWith(handStrength().thenComparing(cardByCard()))
        .mapIndexed { index, (_, bid) ->
          val rank = (index + 1) * 1
          rank * bid
        }
        .sum()
  }

  fun part2(input: List<String>): Int {
    val hands = input.parse()

    return hands
        .sortedWith(handStrength(jokers = true).thenComparing(cardByCard(jokers = true)))
        .mapIndexed { index, (_, bid) ->
          val rank = (index + 1) * 1
          rank * bid
        }
        .sum()
  }

  val testInput1 = readInput("Day07_test1")
  check(part1(testInput1) == 6440)
  check(part2(testInput1) == 5905)

  val input = readInput("Day07")
  part1(input).println()
  part2(input).println()
}

private fun List<String>.parse() = map {
  val (hand, bid) = it.split(" ")
  Pair(hand, bid.toInt())
}

private fun handStrength(jokers: Boolean = false): Comparator<Pair<String, Int>> =
    compareBy { (h) ->
      val hand = if (jokers) replaceJokers(h) else h
      val handCounts = hand.toCharArray().toList().groupingBy { it }.eachCount()
      when {
        fiveOfAKind(handCounts) -> 6
        fourOfAKind(handCounts) -> 5
        fullHouse(handCounts) -> 4
        threeOfAKind(handCounts) -> 3
        twoPair(handCounts) -> 2
        onePair(handCounts) -> 1
        else -> 0
      }
    }

fun cardByCard(jokers: Boolean = false): Comparator<Pair<String, Int>> =
    Comparator { (hand1), (hand2) ->
      val (card1, card2) =
          hand1.toCharArray().zip(hand2.toCharArray()).find { (card1, card2) -> card1 != card2 }
              ?: return@Comparator 0

      cardScore(card1, jokers) - cardScore(card2, jokers)
    }

fun replaceJokers(hand: String): String {
  val handCounts = hand.toCharArray().toList().groupingBy { it }.eachCount().toMutableMap()
  val jokersCount = handCounts['J'] ?: return hand
  if (jokersCount == 5) {
    return hand
  }
  var newHand = hand
  repeat(jokersCount) {
    val (card, count) =
        handCounts
            .filter { (card, count) -> card != 'J' && count < 5 }
            .maxBy { (_, count) -> count }
    newHand = newHand.replaceFirst('J', card)
    handCounts[card] = count + 1
  }
  return newHand
}

private fun cardScore(it: Char, jokers: Boolean = false) =
    when (it) {
      'A' -> 14
      'K' -> 13
      'Q' -> 12
      'J' -> if (jokers) 1 else 11
      'T' -> 10
      '9',
      '8',
      '7',
      '6',
      '5',
      '4',
      '3',
      '2' -> it.toString().toInt()
      else -> throw IllegalArgumentException("Unknown card $it")
    }

fun fiveOfAKind(hand: Map<Char, Int>) = hand.values.find { it == 5 } != null

fun fourOfAKind(hand: Map<Char, Int>) = hand.values.find { it == 4 } != null

fun fullHouse(hand: Map<Char, Int>) =
    hand.values.find { it == 3 } != null && hand.values.find { it == 2 } != null

fun threeOfAKind(hand: Map<Char, Int>) = hand.values.find { it == 3 } != null

fun twoPair(hand: Map<Char, Int>) = hand.values.filter { it == 2 }.size == 2

fun onePair(hand: Map<Char, Int>) = hand.values.filter { it == 2 }.size == 1
