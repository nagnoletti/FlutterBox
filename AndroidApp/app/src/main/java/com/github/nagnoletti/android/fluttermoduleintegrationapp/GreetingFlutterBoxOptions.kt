package com.github.nagnoletti.android.fluttermoduleintegrationapp

import com.github.nagnoletti.android.flutterbox.FlutterBox

data class GreetingFlutterBoxOptions(override val initialRoute: String, val greeting: String) :
    FlutterBox.Options() {
    override val arguments: List<String> = listOf(greeting)
}