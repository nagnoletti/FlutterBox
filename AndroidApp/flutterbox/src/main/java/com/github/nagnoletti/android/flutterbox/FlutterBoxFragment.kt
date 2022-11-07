package com.github.nagnoletti.android.flutterbox

import android.os.Bundle
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger

/**
 * [FlutterFragment] using the active engine from [FlutterBox].
 * A new engine is created and run if a route is passed in this fragment's bundle created with
 * [FlutterBoxFragment.Companion.bundle] during navigation.
 * It uses the current active engine if there is one up and running, defaults to a new cold engine.
 * NOTE: Transition to this fragment crashes if no arguments are passed on navigation.
 */
abstract class FlutterBoxFragment : FlutterFragment() {

    companion object {
        fun bundle(route: String? = null, shouldAutomaticallyHandleBackPresses: Boolean = true) =
            Bundle().apply {
                // Use FlutterFragment argument to let Flutter handle back presses while the fragment
                // is displayed.
                putBoolean(
                    ARG_SHOULD_AUTOMATICALLY_HANDLE_ON_BACK_PRESSED,
                    shouldAutomaticallyHandleBackPresses
                )
                putString(Extras.EXTRA_RESET_ROUTE_TO, route)
            }
    }

    abstract fun configureChannels(bm: BinaryMessenger)

    override fun getCachedEngineId(): String? {
        val resetRouteTo: String? = arguments?.getString(Extras.EXTRA_RESET_ROUTE_TO)
        if (resetRouteTo != null) {
            FlutterBox.resetRoute(requireContext().applicationContext, resetRouteTo)
        }
        return FlutterBox.activeEngineData?.id
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val bm = flutterEngine.dartExecutor.binaryMessenger
        configureChannels(bm)
    }
}