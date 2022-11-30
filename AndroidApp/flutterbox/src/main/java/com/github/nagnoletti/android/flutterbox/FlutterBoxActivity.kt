package com.github.nagnoletti.android.flutterbox

import io.flutter.embedding.android.FlutterActivity
import java.util.*

abstract class FlutterBoxActivity : FlutterActivity() {

    protected open val flutterScreenID = UUID.randomUUID().toString()
    protected open val flutterBoxOptions: FlutterBox.Options? = null

    override fun getCachedEngineId(): String = FlutterBox.getOwnOrNewEngineID(
        this,
        screenID = flutterScreenID,
        opts = flutterBoxOptions
    )
}