package com.donrobo

import java.util.*
import kotlin.collections.ArrayList

fun debug(message: String) {
    System.err.println(message)
}

data class Output(val detectedTimestamps: List<Int>) {
    fun resultOutput(): String {
        val stringBuilder = StringBuilder()
        detectedTimestamps.forEach {
            stringBuilder.appendln(it)
        }
        return stringBuilder.toString()
    }

}

fun detectAsteroids(input: Input): Output {
    return Output(input.images.filter { it.pixels.any { it > 0 } }.map { it.timestamp })
}

data class Image(val timestamp: Int, val width: Int, val height: Int, val pixels: List<Int>) {
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
