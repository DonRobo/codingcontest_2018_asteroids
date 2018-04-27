package com.donrobo

import java.io.File

fun main(args: Array<String>) {
    val inputs = File("src/main/resources/input3")
    inputs.listFiles().forEach { inputFile ->
        val output = detectAsteroids(parseInput(inputFile.readText()))
        val resultOutput = output.resultOutput()

        println(inputFile.name)
        println(resultOutput)
        File(File("output3"), inputFile.name).writeText(resultOutput)
    }
}

