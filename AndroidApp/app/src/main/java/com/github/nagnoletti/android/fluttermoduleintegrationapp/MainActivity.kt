package com.github.nagnoletti.android.fluttermoduleintegrationapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.github.nagnoletti.android.flutterbox.FlutterBox
import io.flutter.embedding.android.FlutterActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Default activity on specific route (new engine)
        findViewById<AppCompatButton>(R.id.button_open_default_flutter_activity_on_route_a).setOnClickListener {
            startActivity(FlutterActivity.withNewEngine().initialRoute("/a").build(this))
        }

        // Custom Flutter activity extending FlutterBoxActivity
        findViewById<AppCompatButton>(R.id.button_open_custom_flutter_activity).setOnClickListener {
            startActivity(Intent(this, CustomFlutterActivity::class.java))
        }

        // Go to an activity that uses a custom fragment extending FlutterBoxFragment
        findViewById<AppCompatButton>(R.id.button_second_activity).setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }
}