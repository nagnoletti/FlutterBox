# FlutterBox - IOS

FlutterBox provides different APIs to simplify usage of the Flutter SDK when [integrating a Flutter module to an IOS application](https://docs.flutter.dev/development/add-to-app/ios/project-setup).

Use it to migrate native apps to Flutter, but keep in mind it manages only one FlutterEngine at a time: every non autoconclusive or multiple entry feature migration is highly discouraged.

## Embed
To embed this tool first **copy it into your native IOS project**. Then generate the Flutter module IOS framework.
To create the library and put it into the app's project run this [script](../../FlutterModule/buildIosFrameworkIntoIosProject.sh).

The script executes the `flutter build ios-framework` command and moves the output [here](../Flutter/).
Copy the podspec files into the `Flutter` folder to be able to include the framework in `Podfile`.

Edit [Podfile](../Podfile) to define the FlutterBox target, providing the Flutter module iOS framework to it  depending on project's configuration (Debug/Release).

Include FlutterBox as a framework in Xcode selecting the app's project and opening *Target > General > Framework, Libraries and EmbeddedContent*.

