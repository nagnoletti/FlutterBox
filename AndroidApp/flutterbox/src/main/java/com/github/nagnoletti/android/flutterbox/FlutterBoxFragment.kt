package com.github.nagnoletti.android.flutterbox

import android.os.Bundle
import io.flutter.embedding.android.FlutterFragment
import java.util.*

abstract class FlutterBoxFragment : FlutterFragment() {

    companion object {
        fun defaultBundle() = Bundle().apply {
            // Use FlutterFragment argument to let Flutter handle back presses while the fragment
            // is displayed.
            putBoolean(ARG_SHOULD_AUTOMATICALLY_HANDLE_ON_BACK_PRESSED, true)
        }
    }

    protected open val flutterScreenID = UUID.randomUUID().toString()
    protected open val flutterBoxOptions: FlutterBox.Options? = null

    override fun getCachedEngineId(): String = FlutterBox.getOwnOrNewEngineID(
        requireContext(),
        screenID = flutterScreenID,
        opts = flutterBoxOptions
    )

}