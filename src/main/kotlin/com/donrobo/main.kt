package com.donrobo

import java.io.File

fun main(args: Array<String>) {
    val inputFile = File("src/main/resources/input/sample1.txt").readText()
    val output = detectAsteroids(parseInput(inputFile))
    val resultOutput = output.resultOutput()

    println(resultOutput)
    File("result.txt").writeText(resultOutput)
}

