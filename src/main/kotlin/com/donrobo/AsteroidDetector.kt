package com.donrobo

import java.util.*
import kotlin.collections.ArrayList

fun debug(message: String) {
    System.err.println(message)
}

fun detectAsteroids(input: Input): Output {
    val sameAsteroids = input.images.map { it.trim().binary() }.groupBy { it.pixels }
    val orbitalTimes = sameAsteroids.filter { it.value.isNotEmpty() && it.key.isNotEmpty() }.mapValues { it.value.map { it.timestamp } }

    val subsets = ArrayList<List<Int>>()

    return Output(subsets.map { AsteroidOccurance(it.min()!!, it.max()!!, it.size) })
}


data class AsteroidOccurance(val firstOccurance: Int, val lastOccurance: Int, val count: Int) {
    fun resultOutput(): String = "$firstOccurance $lastOccurance $count"

}

data class Output(val asteroidOccurances: List<AsteroidOccurance>) {

    fun resultOutput(): String = StringBuilder().apply {
        asteroidOccurances.sortedBy { it.firstOccurance }.forEach {
            appendln(it.resultOutput())
        }
    }.toString()

}

data class Image(val timestamp: Int, val width: Int, val height: Int, val pixels: List<Int>) {

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

        return Image(timestamp, width, rows.size, trimmedPixels)
    }

    private fun trimCols(): Image {
        val cols = cols().filter { it.any { it > 0 } }

        val trimmedPixels = ArrayList<Int>()

        for (y in 0 until height) {
            for (x in 0 until cols.size) {
                trimmedPixels += cols[x][y]
            }
        }

        return Image(timestamp, cols.size, height, trimmedPixels)
    }

    fun trim(): Image {
        return trimRows().trimCols()
    }

    fun binary(): Image = Image(
            timestamp,
            width,
            height,
            pixels.map { if (it > 0) 1 else 0 }
    )

    init {
        if (pixels.size != width * height) throw IllegalArgumentException("Invalid size")
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
        images += Image(timestamp = timestamp, width = colCount, height = rowCount, pixels = pixels)
    }

    return Input(startObservation, endObservation, images)
}
