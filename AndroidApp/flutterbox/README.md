# FlutterBox - Android

FlutterBox provides different APIs to simplify usage of the Flutter SDK
when [integrating a Flutter module to an Android application](https://docs.flutter.dev/development/add-to-app/android/project-setup)
.

Use it to migrate native apps to Flutter, but keep in mind it manages only one FlutterEngine at a
time.

FlutterBox works thanks to caching engines in `FlutterEngineCache` and owner-engine ID pairs. Every
Flutter screen using FlutterBox (activities and/or fragments) should have a unique identifier to
always provide the same engine every time it is displayed. I suggest to extend
`FlutterBoxActivity` and `FlutterBoxFragment` to avoid caring about the engine provisioning logic.

## Embed

To embed this tool first **copy it into your native Android project**, then define some Gradle
properties to project's 'ext' to make the library able to point to the compiled Flutter AAR library
in the shape of local Maven repositories. To create the library and put it into the app's project
run this [script](../../FlutterModule/buildAarIntoAndroidProject.sh).

The script executes the `flutter build aar` command and moves the output [here](../flutter).

See project's [build.gradle](../build.gradle) defining those properties.

## Usage

Initialize FlutterBox before using it (obvious location is your application's `onCreate`).
This is useful to initialize resources to make Flutter screen transitions quicker.

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

Now you have two options to choose from, depending on your needs, described below.

### Extend FlutterBoxActivity or FlutterBoxFragment

You can inherit from
[`FlutterBoxActivity`](src/main/java/com/github/nagnoletti/android/flutterbox/FlutterBoxActivity.kt)
(extending FlutterActivity)
or [`FlutterBoxFragment`](src/main/java/com/github/nagnoletti/android/flutterbox/FlutterBoxFragment.kt)
(extending FlutterFragment) to only care about functional things, such as configuring Flutter
channels.

You can override FlutterActivity and FlutterFragment methods to your need, as FlutterBoxActivity and
FlutterBoxFragment only override the `getCachedEngineId` callback to always retrieve its engine ID
from `FlutterBox.getScreenEngineID`. Here's where the screenID-engineID linking is done.

Those base classes also provide two properties you can override:

- flutterScreenID: provide a custom screen ID used to get the correspondant engine ID.
- flutterBoxOptions: provide an initial route to start the Flutter app and other custom options; if
  you do you can override the `FlutterBox.Options.arguments` property to indicate an ordered
  argument list passed to the Flutter app entrypoint (`main.dart#main` function).

### Make your own FlutterActivity/FlutterFragment

Extend the FlutterSDK's classes to your need. Remember to override the `getCachedEngineId` function
to use `FlutterBox.getScreenEngineID` an engine ID assigned to  