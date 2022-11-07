package com.github.nagnoletti.android.fluttermoduleintegrationapp

import android.app.Application
import com.github.nagnoletti.android.fluttermoduleintegrationlib.FlutterBox

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        FlutterBox.init(this, options = FlutterBox.Options("/a"))
    }
}