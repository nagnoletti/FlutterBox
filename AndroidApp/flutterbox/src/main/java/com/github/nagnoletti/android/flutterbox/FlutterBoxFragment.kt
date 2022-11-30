package com.github.nagnoletti.android.flutterbox

import android.os.Bundle
import io.flutter.embedding.android.FlutterFragment
import java.util.*

/**
 * Simple abstract FlutterFragment retrieving an engineID with its screenID and releasing resources in onDestroy.
 */
abstract class FlutterBoxFragment : FlutterFragment() {

    companion object {
        /**
         * Bundle for fragments running Flutter to let physical back presses take effect on the
         * Flutter app navigation stack while there is more than one entry.
         */
        fun defaultBundle() = Bundle().apply {
            // Use FlutterFragment argument to let Flutter handle back presses while the fragment
            // is displayed.
            putBoolean(ARG_SHOULD_AUTOMATICALLY_HANDLE_ON_BACK_PRESSED, true)
        }
    }

    protected open val screenID = UUID.randomUUID().toString()
    protected open val flutterBoxOptions: FlutterBox.Options? = null

    override fun getCachedEngineId(): String = FlutterBox.getScreenEngineID(
        requireContext(),
        screenID = screenID,
        opts = flutterBoxOptions
    )

    override fun onDestroy() {
        FlutterBox.releaseEngineForScreenID(screenID)
        super.onDestroy()
    }
}