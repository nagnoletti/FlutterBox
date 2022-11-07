package com.github.nagnoletti.android.fluttermoduleintegrationapp

import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import com.github.nagnoletti.android.fluttermoduleintegrationlib.FlutterBoxFragment

class CustomFlutterFragment : FlutterBoxFragment() {

    companion object {
        fun bundle() = bundle("/a")
    }

    override fun configureChannels(bm: BinaryMessenger) {
        val mca = MethodChannel(bm, "method_channel_a")
        mca.setMethodCallHandler { call, result ->
            when (call.method) {
                "ping" -> result.success("pong")
                else -> result.notImplemented()
            }
        }
    }

}