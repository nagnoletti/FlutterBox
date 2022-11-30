package com.github.nagnoletti.android.fluttermoduleintegrationapp

import com.github.nagnoletti.android.flutterbox.FlutterBox
import com.github.nagnoletti.android.flutterbox.FlutterBoxActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class CustomFlutterActivity : FlutterBoxActivity() {

    override val flutterBoxOptions =
        GreetingFlutterBoxOptions("/a", "Hello from owner $screenID! (Activity)")

    override fun getCachedEngineId(): String = FlutterBox.getScreenEngineID(
        this,
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