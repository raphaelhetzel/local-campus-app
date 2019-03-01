# Local Campus App

"Local Campus App" is a prototypical Android App for local campus
communication that we (Alexander Reichmann, Raphael Hetzel) built during the
TUM course "Do-It-Yourself Networking". The App uses the opportunistic
networking middleware SCAMPI for all communication. Furthermore, it allows
users to create various post types using a runtime extension system.

This repository contains the source code for the main Android application and
can serve as a reference/base for future projects. This project is not
maintained in any way and older commits will not compile as we removed some
licensed files from old commits. Furthermore, as this was a research prototype,
it was only built for and tested on Android 7 and 8.1.

The Android devices need to be connected to a common SCAMPI network (we used
Raspberry PIs as local Acess Points/Hubs). These Hubs are also responsible for
broadcasting the Topics available at a location.

## Architecture Documentation

[Architecture Documentation](documentation/architecture.md)

## Building & Installation (Base App)

The base App is a regular Android Studio project consisting of two modules, the main
`app` module and the shared library `localcampuslib`. Furthermore, it links some
extensions that are shipped with the application.

The [Android SDK](https://developer.android.com/studio/#downloads) is required
to build this project. You can either use Android Studio or the command line
tools to acquire the SDK.

To build the Base App, it is sufficient to use the `./gradlew.sh` script
(`./gradlew :app:assembleDebug`) or open and build the project using Android
Studio. (We use Android Studio 3.1) The app contains some tests, which can
be run using the `./gradlew.sh` script (`./gradlew :app:test`) or Android
Studio. The app can be installed using ADB or Android Studio.

Install the App using ADB:

`adb install app/build/outputs/apk/debug/app-debug.apk`

The App needs an instance of ScampiRouter running on the device, the config to
connect to our hubs is included with this repository 
([scampi-router.conf](scampi-router.conf)).

## Extensions

The App has a built-in extension system that allows loading new extensions at
runtime. The extensions are spread over the network. If you don't want to use
the extension system, don't allow the app to access local storage. extensions
are stored in `/sdcard/Downloads/localcampusjars/`). The extensions need to be
stored in this directory to be picked up by the app. They must be named based
on the extension UUID, e.g. `ab6acf96-24bd-4d7d-b9d0-0784e821090b.apk`. Every
version of an extension needs a new UUID. If you manually add an extension to
the extension directory, the app needs to be restarted (force-stop it using
the Android settings). This is not needed if the app receives extensions over
the network.

This project comes with multiple extensions that are not included in the Base
App and therefore need to be loaded by the extension system.

As the extensions are minimal Android applications, they can be built the same
way as you build Android applications (e.g. by running `./gradlew
:votingextension:assembleDebug`  or building the module using Android Studio).
You'll find the extension apk in the output folder (e.g
`votingextension/build/outputs/apk/debug/app-debug.apk`).  The apk then needs
to be renamed to the uuid of the extension, which can be found in the
`Registry` class of the extension. After that, it can be copied to the device.


## Creating Extensions

The extension needs to be created as a regular Android App Module (.apk), as
Android applications contain special `.dex` file, which is needed by the class
loader. If you want to create a new module in this project, you can create an
additional Android App Module by using File->New->New Module and then choosing *Phone &
Tablet Module*.

The Module needs to contain a class `de.tum.localcampusextension.Repository`
with four static fields:

`addPostFragmentClass`: The class of a Fragment to add a new Post, which Extends
the class [AddPostFragment](localcampuslib/src/main/java/de/tum/localcampuslib/AddPostFragment.java).

`showPostFragmentClass`: The class of a Fragment to show a new Post, which Extends the class
[ShowPostFragment](localcampuslib/src/main/java/de/tum/localcampuslib/ShowPostFragment.java). 

`typeDescription`: A short, human-readable description of
the Posts created by the extension, e.g. "Text Post"

`typeId` The UUID of one version of the extension.

Refer to the module [testextension](testextension) for an example extension.

The extensions are able to use Resources contained in the apk file and the system
resources, they can NOT use resources contained in the Base App.
Furthermore, resources can NOT reference other resources (e.g. references in
the layout).

The extensions are able to interact with the data they are related to using the 
[AddPostDataProvider](localcampuslib/src/main/java/de/tum/localcampuslib/AddPostDataProvider.java)
(available in the AddPostFragment) and the 
[ShowPostDataProvider](localcampuslib/src/main/java/de/tum/localcampuslib/ShowPostDataProvider.java)
(available in the ShowPostFragment)

For additional information about the extension system, refer to the
Architecture Documentation.

## Hub & SCAMPI Setup

The app connects to a location dependent Hub. In our Demo, these hubs are
Raspberry Pis running a WIFI access point, a script publishing the available
topics (location-publisher) and an instance of the Scampi Router. The Android
devices connect to this location using a fixed IP address.

The location-publisher needs to publish the available topics to the SCAMPI
service `discovery`. The location messages need to contain the following fields:

```
String: topicName // e.g tum or tum->garching, one location per message
String: deviceId // Identifier of the device (location)
```

The deviceId needs to be configured according to the location setting of the
SCAMPI Router running on the Hub.

If the Hub is configured for the location (Hub SCAMPI Router settings)
```
#    Configure this with at most 3 digits in front of and at most 6 digits after the separator to prevent
# any floating point errors to interfere with the location processing of the app (we currently don't allow any error)
staticLocProvider.latitude = 48.262628
staticLocProvider.longitude = 11.668411
#Broken Settings
staticLocProvider.error = 0.000000
staticLocProvider.elevation = 0.000000
```

the `deviceId` needs to be set to `LAT:+048.262628,LON:+011.668411`.

The Hub also needs to filter some messages (Hub SCAMPI Router settings):
```
#Filters
incomingWhitelistFilter.class = fi.tkk.netlab.dtn.scampi.routing.filters.incoming.ServiceWhitelistFilter
incomingWhitelistFilter.services = extensions, tum, tum->garching

outgoingWhitelistFilter.class = fi.tkk.netlab.dtn.scampi.routing.filters.outgoing.ServiceWhitelistFilter
outgoingWhitelistFilter.services = discovery, extensions, tum, tum->garching

epidemicRouting.incomingContentVectorFilters = incomingWhitelistFilter
epidemicRouting.outgoingContentVectorFilters = outgoingWhitelistFilter

```

The router on the Android device only needs to prevent sharing of discovery messages:
```
#Filters
outgoingBlacklistFilter.class = fi.tkk.netlab.dtn.scampi.routing.filters.outgoing.ServiceBlacklistFilter
outgoingBlacklistFilter.services = discovery

epidemicRouting.outgoingContentVectorFilters = outgoingBlacklistFilter
```

## License

Local Campus App and the bundled extensions are released under the [MIT license](LICENSE).