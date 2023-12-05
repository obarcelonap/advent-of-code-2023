import kotlin.math.min
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {

  fun part1(input: List<String>): Long {
    val seeds = input.parseSeeds()
    val seedToLocation = parseSeedToLocationConversion(input.drop(1))

    return seeds.minOfOrNull { seedToLocation(it) } ?: 0
  }

  fun part2(input: List<String>): Long {
    val seeds = input.parseSeeds().inRanges()
    val seedToLocation = parseSeedToLocationConversion(input.drop(1))

    return runBlocking(Dispatchers.Default) {
      val channel = Channel<Long>()
      launch {
        for (seed in seeds) {
          launch { channel.send(seedToLocation(seed)) }
        }
        delay(5.seconds) // to avoid chanel being close while last seed is being processed
        channel.close()
      }

      var received = 0L
      var lowestLocation = -1L
      println("receiving")
      for (location in channel) {
        received++
        lowestLocation = if (lowestLocation == -1L) location else min(lowestLocation, location)
        if (received % 1_000_000 == 0L) {
          print(".")
          if (received % 100_000_000 == 0L) {
            received.println()
          }
        }
      }
      println("")
      println("done $received")
      lowestLocation
    }
  }

  val testInput1 = readInput("Day05_test1")
  check(part1(testInput1) == 35L)
  check(part2(testInput1) == 46L)

  val input = readInput("Day05")
  part1(input).println()
  part2(input).println()
}

private fun List<String>.parseSeeds() =
    first().split(":")[1].trim().split(" ").asSequence().map { it.toLong() }

private fun Sequence<Long>.inRanges(): Sequence<Long> =
    chunked(2) { (start, length) -> start ..< (start + length) }.flatMap { it }

private fun parseSeedToLocationConversion(input: List<String>): (Long) -> Long {
  var seedToSoil = mapOf<LongRange, LongRange>()
  var soilToFertilizer = mapOf<LongRange, LongRange>()
  var fertilizerToWater = mapOf<LongRange, LongRange>()
  var waterToLight = mapOf<LongRange, LongRange>()
  var lightToTemperature = mapOf<LongRange, LongRange>()
  var temperatureToHumidity = mapOf<LongRange, LongRange>()
  var humidityToLocation = mapOf<LongRange, LongRange>()

  val ite = input.iterator()
  while (ite.hasNext()) {
    val line = ite.next()
    when {
      line.startsWith("seed-to-soil map:") -> seedToSoil = ite.parseNextMap()
      line.startsWith("soil-to-fertilizer map:") -> soilToFertilizer = ite.parseNextMap()
      line.startsWith("fertilizer-to-water map:") -> fertilizerToWater = ite.parseNextMap()
      line.startsWith("water-to-light map:") -> waterToLight = ite.parseNextMap()
      line.startsWith("light-to-temperature map:") -> lightToTemperature = ite.parseNextMap()
      line.startsWith("temperature-to-humidity map:") -> temperatureToHumidity = ite.parseNextMap()
      line.startsWith("humidity-to-location map:") -> humidityToLocation = ite.parseNextMap()
    }
  }

  return { seed: Long ->
    val soil = seedToSoil.convert(seed)
    val fertilizer = soilToFertilizer.convert(soil)
    val water = fertilizerToWater.convert(fertilizer)
    val light = waterToLight.convert(water)
    val temperature = lightToTemperature.convert(light)
    val humidity = temperatureToHumidity.convert(temperature)
    val location = humidityToLocation.convert(humidity)
    location
  }
}

private fun Map<LongRange, LongRange>.convert(value: Long): Long {
  val (source, destination) =
      entries.firstOrNull { (source) -> source.contains(value) } ?: return value

  return destination.first + (value - source.first)
}

private fun Iterator<String>.parseNextMap(): Map<LongRange, LongRange> {
  val map = mutableMapOf<LongRange, LongRange>()
  while (hasNext()) {
    val line = next()
    if (line.trim().isBlank()) {
      return map
    }
    val (dstStart, srcStart, length) = line.trim().split(" ").map { it.toLong() }
    map[srcStart ..< (srcStart + length)] = dstStart ..< (dstStart + length)
  }
  return map
}
