package com.donrobo

import java.util.*
import kotlin.collections.ArrayList

fun debug(message: String) {
    System.err.println(message)
}

fun detectAsteroids(input: Input): Output {
    val sameAsteroids = input.images.map { it.trim().binary().rotationNormalized() }.groupBy { it.rotatedBy(-it.rotated).pixels }
    val orbitalTimes = sameAsteroids.filter { it.value.isNotEmpty() && it.key.isNotEmpty() }

    val subsets = ArrayList<List<Int>>()

    orbitalTimes.values.forEach {
        val unaccountedFor = it.sortedBy { it.timestamp }.toMutableList()
        while (unaccountedFor.size >= 4) {
            val maxOrbit = (input.endObservation - input.startObservation + 1) / 3
            val t = unaccountedFor.minBy { it.timestamp }!!
            val offset = t.timestamp - input.startObservation

            val possibleOrbits = unaccountedFor.map { it.timestamp - t.timestamp }.filter { it in 1..maxOrbit }

            orbitSearchLoop@
            for (period in possibleOrbits) {
                if (unaccountedFor.size < 4) break@orbitSearchLoop
                if ((input.endObservation - t.timestamp) / period > unaccountedFor.size) continue
                if (offset > period - 1)
                    continue

                val expected = (t.timestamp..input.endObservation step period).map { it }
                if (unaccountedFor.map { it.timestamp }.containsAll(expected)) {
                    for (rotatingBy in 0..3) {
                        var currentRotation = t
                        var ok = true
                        unaccountedFor.filter { it.timestamp in expected }.sortedBy { it.timestamp }.forEach {
                            if (ok) {
                                if (it.pixels != currentRotation.pixels) {
                                    ok = false
                                }
                                currentRotation = currentRotation.rotatedBy(rotatingBy)
                            }
                        }
                        if (ok) {
                            subsets += expected
                            unaccountedFor.removeIf { it.timestamp in expected }
                            break@orbitSearchLoop
                        }
                    }
                }
            }
            unaccountedFor.remove(t)
        }
    }

    return Output(subsets.map { AsteroidOccurrence(it.min()!!, it.max()!!, it.size) })
}


data class AsteroidOccurrence(val firstOccurrence: Int, val lastOccurrence: Int, val count: Int) {
    fun resultOutput(): String = "$firstOccurrence $lastOccurrence $count"

}

data class Output(val asteroidOccureences: List<AsteroidOccurrence>) {

    fun resultOutput(): String = StringBuilder().apply {
        asteroidOccureences.sortedBy { it.firstOccurrence }.forEach {
            appendln(it.resultOutput())
        }
    }.toString()

}

enum class RotationAxis { X, Y, Z }

data class Image(val timestamp: Int, val width: Int, val height: Int, val depth: Int, val pixels: List<Int>, val rotated: Int = 0, val axis: RotationAxis = RotationAxis.Z) {

    private fun rows(): List<List<Int>> {
        val rows = ArrayList<List<Int>>()

        for (y in 0 until height) {
            val row = ArrayList<Int>()
            for (x in 0 until width) {
                row += getPixel(x, y)
            }
            rows += row
        }

        return rows
    }

    private fun cols(): List<List<Int>> {
        val cols = ArrayList<List<Int>>()

        for (x in 0 until width) {
            val col = ArrayList<Int>()
            for (y in 0 until height) {
                col += getPixel(x, y)
            }
            cols += col
        }

        return cols
    }

    fun getPixel(x: Int, y: Int): Int = pixels[x + y * width]

    private fun trimRows(): Image {
        val rows = rows().filter { it.any { it > 0 } }

        val trimmedPixels = ArrayList<Int>()

        for (y in 0 until rows.size) {
            for (x in 0 until width) {
                trimmedPixels += rows[y][x]
            }
        }

        return Image(timestamp, width, rows.size, depth, trimmedPixels)
    }

    private fun trimCols(): Image {
        val cols = cols().filter { it.any { it > 0 } }

        val trimmedPixels = ArrayList<Int>()

        for (y in 0 until height) {
            for (x in 0 until cols.size) {
                trimmedPixels += cols[x][y]
            }
        }

        return Image(timestamp, cols.size, height, depth, trimmedPixels)
    }

    fun trim(): Image {
        return trimRows().trimCols()
    }

    fun binary(): Image = Image(
            timestamp,
            width,
            height,
            depth,
            pixels.map { if (it > 0) 1 else 0 }
    )

    fun rotationNormalized(): Image {
        val rotations = (0..3).map { rotatedBy(it) }.map { Pair(it, it.imageHash()) }
        val original = rotations.minBy { it.second }!!.first
        var neededRotations = 0
        while (original.rotatedBy(neededRotations).pixels != pixels) {
            neededRotations++
        }
        return Image(timestamp, width, height, depth, pixels, neededRotations)
    }

    private fun imageHash(): String = pixels.map { it.toString(16) }.joinToString()

    fun rotatedBy(rotations: Int): Image = when (rotations) {
        0 -> this
        1 -> {
            val newPixels = ArrayList<Int>()

            for (y in 0 until width) {
                for (x in 0 until height) {
                    newPixels += getPixel(width - 1 - y, x)
                }
            }

            Image(timestamp, height, width, depth, newPixels, if (rotated == 3) 0 else rotated + 1)
        }
        else -> if (rotations > 1) rotatedBy(1).rotatedBy(rotations - 1) else rotatedBy(rotations + 4)
    }

    init {
        if (pixels.size != width * height * depth) throw IllegalArgumentException("Invalid size")
        if (rotated !in 0..3) throw IllegalArgumentException("$rotated is invalid rotation count")
    }
}

data class Input(val startObservation: Int, val endObservation: Int, val images: List<Image>)

fun parseInput(inputStr: String): Input {
    val input = Scanner(inputStr)
    val startObservation = input.nextInt()
    val endObservation = input.nextInt()
    val imageCount = input.nextInt()

    val images = ArrayList<Image>()
    for (image in 0 until imageCount) {
        val timestamp = input.nextInt()
        val rowCount = input.nextInt()
        val colCount = input.nextInt()
        val pixels = ArrayList<Int>()
        for (row in 0 until rowCount) {
            for (col in 0 until colCount) {
                val intensity = input.nextInt()
                pixels += intensity
            }
        }
        images += Image(timestamp = timestamp, width = colCount, height = rowCount, depth = 1, pixels = pixels)
    }

    return Input(startObservation, endObservation, images)
}
