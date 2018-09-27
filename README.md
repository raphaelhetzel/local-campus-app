# Local Campus App

Extensible App for Campus Communication. This Repository contains the Android
app and the issue tracker. Please refer to our 
[pi-gen](https://gitlab.com/diy-networking-team2/pi-gen)
repository for the router/hub images and the
[location-publisher](https://gitlab.com/diy-networking-team2/location-publisher)
repository for the application running on the router responsible
for broadcasting the topics available at a a hub.

## Architecture Documentation
TODO: move doc to repository

## Building & Installation (Base App)

The Base app is a regular Android project consisting of two modules, the main
*app* module and the shared library *localcampuslib*. Futhermore it links some
extensions that are shipped with the application. To build the app, it is
sufficient to build the app using the gradlew script (*./gradlew
assembleDebug*) or Android studio. (We use Android studio 3.1) The app
contains some tests, which can also be run using gradlew (*./gradlew test*)
and Android Studio. The app can be installed using adb or Android Studio.

The app needs an instance of Scampi router running on the device, the config
to connect to our hubs is included with this repository (*scampi-router.conf*).

## Creating Extensions

The App has a built in Extension system that allows loading Extensions at
Runtime. The Extensions are spread over the network. If you don't wan't to use
the extension system, don't allow the app to acess local storage. Extensions
are stored in */sdcard/Downloads/localcampusjars/*). The Extensions need to be
stored in this directory (and the app needs to be restarted (force stop)) to
be picked up by the app. They must be named after the extension UUID, e.g.
*ee5afd62-6e72-4728-8404-e91d7ea2c303.apk*. Every Version of an Extension
needs a new UUID.

The Extension needs to be created as a regular android app (.apk, Android
applications contain special dex file, which is used by the class loader.),
containing a class *Repository* with the three static fields
*addPostFragmentClass* (the class of a Fragment to add a new Post based on the
class AddPostFragment), *showPostFragmentClass* (the class of a Fragment to
show a new Post based on the class AddPostFragment),     *typeDescription* (A
short, human-readable description of the Posts created by the extension, e.g.
"Text Post")     and *typeId* (The UUID of one version of the extension).
Refer to the module *testextensions* for an example extension.

Extensions are able to use Resources contained in the apk file and the system
resources, they can NOT reference resources contained in the base application.
Furthermore, resources can NOT refernce other resources (e.g. references in
the layout).

To use build the Extension, run the *assemble* task of the extension modules
gradle project (e.g by creating a new run configuration in Android Studio).
The extension can then be found in the build directory
(*build/outputs/apk/debug/modulename-debug.apk*). This file need to be renamed
to reflect the extension UUID and then copied to the device.

For additional information about the extension system, refer to the
Architecture documentation.

## Router / Hub

The App needs a location dependent hub. In our demo Project, this hubs are
Raspberry Pis running an WIFI acess point, the location publisher and a
instance of the Scampi Router. The Android device needs to be connected to the
WIFI created by the Raspberry. The Scampi config provided with this project
will connect to the preconfigured, fixes IP of the Scampi Router.

Wifi Settings:

SSID: praktikumdiy
Password: praktikumdiy

Optianally Configure any Proxy settings needed for the Network the Raspberry
is connected to, as it will share this with the WIFI devives. This is not
needed for the App to function correctly.
