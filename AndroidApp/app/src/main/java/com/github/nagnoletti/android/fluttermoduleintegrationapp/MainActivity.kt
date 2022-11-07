package com.github.nagnoletti.android.fluttermoduleintegrationapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.github.nagnoletti.android.fluttermoduleintegrationlib.FlutterBox
import com.github.nagnoletti.android.fluttermoduleintegrationapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Default activity on current running route (no new engines)
        findViewById<AppCompatButton>(R.id.button_open_default_flutter_activity_running_route).setOnClickListener {
            FlutterBox.launchDefaultActivityIntent(this)
        }

        // Default activity on specific route (new engine)
        findViewById<AppCompatButton>(R.id.button_open_default_flutter_activity_on_route_a).setOnClickListener {
            FlutterBox.launchDefaultActivityIntent(this, "/a")
        }

        // Custom Flutter activity extending FlutterBoxActivity
        findViewById<AppCompatButton>(R.id.button_open_custom_flutter_activity).setOnClickListener {
            startActivity(CustomFlutterActivity.intent(this))
        }

        // Go to an activity that uses a custom fragment extending FlutterBoxFragment
        findViewById<AppCompatButton>(R.id.button_second_activity).setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }
}