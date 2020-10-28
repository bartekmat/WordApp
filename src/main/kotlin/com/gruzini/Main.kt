package com.gruzini

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.reactfx.EventStreams
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.Executors
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

val thread = Executors.newSingleThreadExecutor()

class App : Application(){
    val ngrams = ConcurrentSkipListSet<NGram>() //nice concurrent alternative to TreeSet - much faster

    override fun start(primaryStage: Stage) {
        load()
        val edit = TextField()
        val completionList = ListView<String>()

        val vbox = VBox(edit, completionList).apply {
            spacing = 15.0
            padding = Insets(15.0)
        }

//        edit.textProperty().addListener {observer ->
//            completionList.items.setAll(ngrams.complete(edit.text))
//        }

        val edits = EventStreams.valuesOf(edit.textProperty()).forgetful()
        val fewerEdits = edits.successionEnds(Duration.ofMillis(500)).filter {it.isNotBlank()}

        val inBackground = fewerEdits.threadBridgeToFx(thread)
        val completions = inBackground.map { ngrams.complete(it) }
        val inForeground = completions.threadBridgeToFx(thread)

        val lastResult =
            inForeground.map { FXCollections.observableArrayList(it) }.toBinding(FXCollections.emptyObservableList())

        completionList.itemsProperty().bind(lastResult)

        primaryStage.scene = Scene(vbox)
        primaryStage.show()
    }

    private fun load() {
        val path = Paths.get("words.txt")
        val time = measureTimeMillis {
            Files.lines(path).parallel().forEach { line ->
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
    exitProcess(0)
}