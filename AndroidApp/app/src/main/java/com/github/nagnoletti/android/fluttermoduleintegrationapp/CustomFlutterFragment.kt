package com.github.nagnoletti.android.fluttermoduleintegrationapp

import com.github.nagnoletti.android.flutterbox.FlutterBox
import com.github.nagnoletti.android.flutterbox.FlutterBoxFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class CustomFlutterFragment : FlutterBoxFragment() {

    companion object {
        fun bundle() = defaultBundle()
    }

    override val screenID = "Very unique ID"
    override val flutterBoxOptions =
        GreetingFlutterBoxOptions("/a", "Hello from owner $screenID! (Fragment)")

    override fun getCachedEngineId(): String = FlutterBox.getScreenEngineID(
        requireContext(),
        screenID = screenID,
        opts = flutterBoxOptions
    )

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        val mca = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "method_channel_a")
        mca.setMethodCallHandler { call, result ->
            when (call.method) {
                "ping" -> result.success("pong")
                else -> result.notImplemented()
            }
        }
    }

}