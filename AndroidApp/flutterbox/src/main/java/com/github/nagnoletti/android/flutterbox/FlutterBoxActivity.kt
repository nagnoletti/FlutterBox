package com.github.nagnoletti.android.flutterbox

import io.flutter.embedding.android.FlutterActivity
import java.util.*

/**
 * Simple abstract FlutterActivity retrieving an engineID with its screenID and releasing resources in onDestroy.
 */
abstract class FlutterBoxActivity : FlutterActivity() {

    protected open val screenID = UUID.randomUUID().toString()
    protected open val flutterBoxOptions: FlutterBox.Options? = null

    override fun getCachedEngineId(): String = FlutterBox.getScreenEngineID(
        this,
        screenID = screenID,
        opts = flutterBoxOptions
    )

    override fun onDestroy() {
        FlutterBox.releaseEngineForScreenID(screenID)
        super.onDestroy()
    }
}