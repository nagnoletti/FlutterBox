# FlutterBox - Android

FlutterBox provides different APIs to simplify usage of the Flutter SDK when [integrating a Flutter module to an Android application](https://docs.flutter.dev/development/add-to-app/android/project-setup).

## Embed
To embed FlutterBox first **copy it into your native Android project**, the define some Gradle properties to project's 'ext' to make the library able to point to the compiled Flutter AAR library in the shape of local Maven repositories.
To create the library and put it into the app's project run this [script](../../FlutterModule/buildAarIntoAndroidProject.sh).

The script executes the `flutter build aar` command and moves the output [here](../flutter/).

See project's [build.gradle](../build.gradle) defining those properties.

## Usage

To initialize Flutter and maybe pre-warm an engine just call `FlutterBox.init` where you need to.
**It is meant to be called only once**, otherwise a `FlutterBox.Exception` is thrown.
It makes sense to do it in your `Application` class' `onCreate`.

You can also provide launch options using `FlutterBox.Options`. In this case the Flutter app should open at route "/a".

``` kotlin
override fun onCreate() {
        super.onCreate()

        FlutterBox.init(this, options = FlutterBox.Options("/a"))
}
```

Then, open a default `FlutterActivity` providing optional route argument for the Flutter app:
``` kotlin
button.setOnClickListener {
    FlutterBox.launchDefaultActivityIntent(context, "/a")
}
```

You can also launch custom `FlutterActivity` or `FlutterFragment` instances if you need to configure some channels or any other need that applies (for example you are using the Jetpack Navigation library).
To do so, extend `FlutterBoxActivity` and `FlutterBoxFragment` (they interact with FlutterBox automatically and implement default behavior).