package com.github.nagnoletti.android.fluttermoduleintegrationlib

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.view.WindowCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BinaryMessenger

/**
 * [FlutterActivity] using the active engine from [FlutterBox].
 * A new engine is created and run if a route is passed in this activity's intent created with
 * [FlutterBoxActivity.Companion.intent].
 * It uses the current active engine if there is one up and running, defaults to a new cold engine.
 */
abstract class FlutterBoxActivity : FlutterActivity() {

    companion object {
        fun intent(context: Context, javaClass: Class<out Activity>, route: String? = null) =
            Intent(context, javaClass).apply {
                putExtra(Extras.EXTRA_RESET_ROUTE_TO, route)
            }
    }

    abstract fun configureChannels(bm: BinaryMessenger)

    override fun getCachedEngineId(): String? {
        val resetRouteTo: String? = intent?.getStringExtra(Extras.EXTRA_RESET_ROUTE_TO)
        if (resetRouteTo != null) {
            FlutterBox.resetRoute(applicationContext, resetRouteTo)
        }
        return FlutterBox.activeEngineData?.id
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aligns the Flutter view vertically with the window.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Disable the Android splash screen fade out animation to avoid
            // a flicker before the similar frame is drawn in Flutter.
            splashScreen.setOnExitAnimationListener { splashScreenView -> splashScreenView.remove() }
        }

        super.onCreate(savedInstanceState)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        val bm = flutterEngine.dartExecutor.binaryMessenger
        configureChannels(bm)

        super.configureFlutterEngine(flutterEngine)
    }
}