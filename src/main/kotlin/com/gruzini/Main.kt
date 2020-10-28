package com.gruzini

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.system.measureTimeMillis


class App : Application(){
    val ngrams = TreeSet<NGram>()

    override fun start(primaryStage: Stage) {
        load()
        val edit = TextField()
        val completionList = ListView<String>()

        val vbox = VBox(edit, completionList).apply {
            spacing = 15.0
            padding = Insets(15.0)
        }

        edit.textProperty().addListener {observer ->
            completionList.items.setAll(ngrams.complete(edit.text))
        }

        primaryStage.scene = Scene(vbox)
        primaryStage.show()
    }

    private fun load() {
        val path = Paths.get("words.txt")
        val time = measureTimeMillis {
            Files.lines(path).forEach { line ->
                val words = line.substringAfter("\t").replace("\t", " ").toLowerCase()
                val freq = line.substringBefore("\t").toInt()
                val nGram = NGram(words, freq)
                ngrams.add(nGram)
            }
        }
        println("time = $time")
    }
}


fun main(args: Array<String>){
   Application.launch(App::class.java, *args)
}