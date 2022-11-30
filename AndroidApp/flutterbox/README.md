# FlutterBox - Android

FlutterBox provides different APIs to simplify usage of the Flutter SDK when [integrating a Flutter module to an Android application](https://docs.flutter.dev/development/add-to-app/android/project-setup).

Use it to migrate native apps to Flutter, but keep in mind it manages only one FlutterEngine at a time: every non autoconclusive or multiple entry feature migration is highly discouraged.

## Embed
To embed this tool first **copy it into your native Android project**, then define some Gradle properties to project's 'ext' to make the library able to point to the compiled Flutter AAR library in the shape of local Maven repositories.
To create the library and put it into the app's project run this [script](../../FlutterModule/buildAarIntoAndroidProject.sh).

The script executes the `flutter build aar` command and moves the output [here](../flutter).

See project's [build.gradle](../build.gradle) defining those properties.

## Usage
Initialize FlutterBox before using it (obvious location is your application's `onCreate`).
``` kotlin
import android.app.Application
import com.github.nagnoletti.android.flutterbox.FlutterBox

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        FlutterBox.initialize(this)
    }
}
```

Now 