package com.github.nagnoletti.android.fluttermoduleintegrationapp

import android.content.Context
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import com.github.nagnoletti.android.fluttermoduleintegrationlib.FlutterBoxActivity

class CustomFlutterActivity : FlutterBoxActivity() {

    companion object {
        fun intent(context: Context) = intent(context, CustomFlutterActivity::class.java, "/a")
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