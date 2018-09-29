# Local Campus App

Extensible App for campus communication. This Repository contains the Android
app and the issue tracker. Please refer to our  [pi-gen](https://gitlab.com/diy-networking-team2/pi-gen)
repository for our fork of [pi-
gen](https://github.com/RPi-Distro/pi-gen), which is used to build our
router/hub images and the [location-publisher](https://gitlab.com/diy-networking-team2/location-publisher)
repository for the application running on
the router/hub which is responsible for broadcasting the topics available at a this
location.

## Architecture Documentation

[Architecture Documentation](documentation/architecture.md)

## Building & Installation (Base App)

The Base App is a regular Android project consisting of two modules, the main
`app` module and the shared library `localcampuslib`. Futhermore it links some
extensions that are shipped with the application.

The [Android SDK](https://developer.android.com/studio/#downloads) is required
to build this project. You can either use Android Studio or the command line
tools to aquire the SDK. For an example on how to aquire the SDK using the
command line tools, please refer to the file [.gitlab-ci.yml](.gitlab-ci.yml).

To build the Base App, it is sufficient to use the `./gradlew.sh` script
(`./gradlew :app:assembleDebug`) or open and build the project using Android
Studio. (We use Android Studio 3.1) The app contains some tests, which can
be run using the `./gradlew.sh` script (`./gradlew :app:test`) or Android
Studio. The app can be installed using adb or Android Studio.

Install the App using adb:

`adb install app/build/outputs/apk/debug/app-debug.apk`

The app needs an instance of ScampiRouter running on the device, the config to
connect to our hubs is included with this repository 
([scampi-router.conf](scampi-router.conf)).

## Extensions

The app has a built in extension system that allows loading extensions at
runtime. The extensions are spread over the network. If you don't wan't to use
the extension system, don't allow the app to acess local storage. Extensions
are stored in `/sdcard/Downloads/localcampusjars/`). The extensions need to be
stored in this directory to be picked up by the app. They must be named based on the
the extension UUID, e.g. `ab6acf96-24bd-4d7d-b9d0-0784e821090b.apk`. Every
version of an extension needs a new UUID. If you manually add an extension to
the extension directory, the app needs to be restarted (force-stop it using
the Android settings). This is not needed if the app receives extensions over the network.

This project comes with multiple extensions that are not included in the Base
App and therefore need to be loaded by the extension system. Some of them have
a precompiled apk available in the [extension_apks/](extension_apks) folder.

As the extensions are minimal Android applications, they can be build the same
way as you build Android applications (e.g. by running `./gradlew :votingextension:assembleDebug` 
or building the module using Android Studio). 
You'll find the extension apk in the output folder
(e.g `votingextension/build/outputs/apk/debug/app-debug.apk`). 
The apk then needs to be renamed to the uuid of the extension,
which can be found in the `Registry` class of the extension. After that it can be copied to the device.


## Creating Extensions

The Extension needs to be created as a regular Android App Module (.apk), as
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

The Extensions are able to use Resources contained in the apk file and the system
resources, they can NOT use resources contained in the Base App.
Furthermore, resources can NOT reference other resources (e.g. references in
the layout).

The Extensions are able to interact with the data they are related to using the 
[AddPostDataProvider](localcampuslib/src/main/java/de/tum/localcampuslib/AddPostDataProvider.java)
(available in the AddPostFragment) and the 
[ShowPostDataProvider](localcampuslib/src/main/java/de/tum/localcampuslib/ShowPostDataProvider.java)
(available in the ShowPostFragment)

For additional information about the extension system, refer to the
Architecture Documentation.

## Router / Hub

The app needs a location dependent router / hub. In our Demo, this hubs are
Raspberry Pis running an WIFI Acess Point, the [location-publisher](https://gitlab.com/diy-networking-team2/location-publisher)
and an instance of the Scampi Router. The Android device needs to be connected
to the WIFI Network provided created by the Raspberry.
The Scampi config provided with this project will connect to the preconfigured,
fixes IP on which the Scampi Router is running.

Wifi Settings:

SSID: praktikumdiy  
Password: praktikumdiy

Optionally configure any proxy settings needed for the network the Raspberry
is connected to on the android device, as the Raspberry will share this with
the WIFI devives. This is not needed for the App to function correctly,
only to provide the device with internet acess while it is connected to the hub.
