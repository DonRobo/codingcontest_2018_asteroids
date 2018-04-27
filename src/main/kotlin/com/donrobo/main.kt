package com.donrobo

import java.io.File

fun main(args: Array<String>) {
    val level = 5
    val inputs = File("src/main/resources/input$level")
    inputs.listFiles()
//            .filter { it.name == "lvl5-1.inp" }
            .forEach { inputFile ->
        val output = detectAsteroids(parseInput(inputFile.readText()))
        val resultOutput = output.resultOutput()

        println(inputFile.name)
        println(resultOutput)
                File(File("output$level"), inputFile.name).writeText(resultOutput)
    }
}

