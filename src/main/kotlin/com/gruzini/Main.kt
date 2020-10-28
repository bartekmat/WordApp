package com.gruzini

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage


class App : Application(){
    override fun start(primaryStage: Stage) {
        val edit = TextField()
        val completionList = ListView<String>()

        val vbox = VBox(edit, completionList).apply {
            spacing = 15.0
            padding = Insets(15.0)
        }

        primaryStage.scene = Scene(vbox)
        primaryStage.show()
    }
}


fun main(args: Array<String>){
    Application.launch(App::class.java, *args)
}