# Description of the Application

The initial goal of the application was to provide a local version of the
campus communication app [Jodel](https://jodel.com/), extended with additional
ways to interact with the environment (e.g. the cafeteria or the thermostat of
a room). In order to support various ways to interact with the environment,
instead of creating a application with a fixed number of interactions, we
created the application so that it supports user created applications that are
shared over the network. Instead of text and image posts as they exist in
Jodel, the feeds in our application consist of Posts that allow to interact
with the environment in various ways. The communication channels, which are
called *Topics* in our Application, are tiered to the location, and
interaction is only possible with those Topics, which are relevant to the
users location.

# System Architecture

Our Application consists of android devices with our application installed
acting as the users interface with our system and raspberry pies acting as
hubs for a specific location. They communicate over *Scampi*, which is a Delay
Tolerant and Oppurtunistic Networking Middleware. For Scampi to work, each
device has to run the Scampi router application. Scampi allows the devices
to store and carry messages with them and therefore share them with multiple
hubs. This is important as some messages are relevant for the whole campus and
therefore need to be transfered between multiple hubs/rooms. In our
architecture, there SHOULD be one hub for every distinct location, e.g. a room
or the cafeteria. The android devices don't communicate directly, instead they
only interact with the hubs. The hubs also aren't connected in any way, they
only receive messages from the clients interacting with them.

In a basic, first version of the application, the hubs act as Wireless Access
Point which have the Scampi router application running on a well known IP
and port. When available, they also share their wired ethernet connection with
the devices conncted to the wireless network. In a future version of the
application, the hubs could be connected to the campus network, in which case
the client devices could stay connected to the more powerfull campus Network
as well. The hubs would then need to be identified and connected to usign
another method, one solution would be the broadcast their ip over bluetooth
beacons, which the Scampi router application would then connect to. Even if
this additional method to connect to a hub is added, the basic connectivity
method should remain, so that the application can independently of the
exisiting infrastructure.

# Entities / Message Types

Our application has multiple important entities.

## Topic
The most general entity is a Topic, which defines a channel of communication,
e.g. `/tum` or `/tum/garching`. While there is a structure to this topic
names, the system threats threated independently of each other (the logic for
tiering could be added to the application layer, as it is for example done by
the publishing application), as each of the represents a *Scampi service*, and
those services don't support any tiering. Topics are shared over the
`discovery` service, which is populated by a special publishing application
running on every hub. They MUST not be shared by the android devices, they
should only come directly from the hub the client is connected to.

```
String: topicName // e.g /tum or /tum/garching
String :deviceIdentifier // Identifier of the device (location)
```
Topic Message on the `discovery` service

## Post
The most important entity in our system is a *Post*. It corresponds to one
root message and is published to one Topic. It contains meta information like
the creation date and the creators id and the data, which is sent as a string
and parsed depending on the *PostType*. The data MUST be encoded as JSON and
contain the two mandatory keys `text` and `color`, which are used to display
the list of Posts. The rest of the data is defined by the PostType and can
therefore only be parse by the corresponding extension.

```
String: message_type = post
String: topic // e.g. /tum
String: uuid
String: creator
Integer: created_at // timestamp
String: data // encoded as json, needs the to contain the fields "text" and "color"
String: type_id // Post type, identifing the extension required to show this post
```
Post message on a Topic service

## Vote
There is a score attached to every Post, the users are allowed to rate the
Post either positive or negative, and the Posts are sorted depending on this
score. This score is calculated from the *Votes*, another entity in the
system. Each Vote is transfered as it's own message. The Votes
are sent to the same Topic service as the post they belong to.

```
String: message_type = vote
String: uuid
String: post_uuid
String: creator
Integer: created_at
Integer: score_influence
```
Vote message on a Topic service

## Post Extension
Furthermore, there can be multiple `PostExtensions` attached to a Post,
which contain similar fields like the Post itself and are only parsed by the
extension correponding to the posts PostType. A PostExtension could for
example be a Comment attached to a Text Post, which allows interaction similar
to the one provided by Jodel. The post extensions are also spread over the
same service as the Posts and Votes.

```
String: message_type = post_extension
String: uuid
String: post_uuid
String: creator
Integer: created_at
String: data // encoded as json, no required fields
```
PostExtension message on a Topic service

## Extension
The extensions needed to show `PostTypes` that are not shipped with the
applicaion by default need to be shared between devices. This happens over
simple messages containing the extension apk file and the extensions UUID. If
a extension needs be updated, it needs to be published with a new UUID, and
therefore be threated as a new extension. The extensions are shared over a
special `extensions` service.

```
String: message_type = extension
String: uuid
Binary: binary
```
PostExtension message on the `extensions` service

## General remark around the Entities

While at first we planned to update the posts to include PostExtensions and Votes,
and merge them if there were concurent changes, which would lead to a
situation always only have one self stabelizing message per post in the
system, we decided to use seperate messages for Votes and PostExtensions to
reduce the network overhead. Furthermore, updating messages on remote nodes is
not supported by the communication framework, therefore the outdated messages
would remain on the remote systems. Finally, in order to be able to merge
posts they would need to be designed in a special way (e.g. no hard deletes),
which would lead to complext messages and a generall complexity similar to the
one created by the need for seperate messages for those changes / additions.

In order to support post types that contain complex binary data, another
entity type *BinaryPost* should be added to the system which is similar to the
regular Post Entity, but contains an additional binary field which can be used
to e.g. transfer an image. As this has different requirements to  the device
(e.g. storage permissions / storage space), and in order to prevent backwars
compabilty issues, this should be threated as a new entity compared to
updating the existing entity.

# Android Application Architecture

The Android application is structured in such a way that every Post,
PostExtension and Vote shown in the UI has beed received over the network,
even the ones created by the app itself, ehich leads to a reproducible data
flow.

The UI and application logic (e.g. parsing the various post types) side of the
application consists of a regular Android application based on [Android
Jetpack Components](https://developer.android.com/jetpack/). These layers use
[LiveData](https://developer.android.com/topic/libraries/architecture/livedata
) from a sqllite database queried and managed by the [Room ORM
library](https://developer.android.com/topic/libraries/architecture/room).
This results in an UI that is automatically updated when the data in the
Database is updated. This data acess is further encapsultes via a Repository
Layer, which ensures the data flow described in the previous paragraph.
Furthermore, the repository layer with it's well defined interface allows easy
replacement of the actual data implementation with a local in- memory variant
which is not using the network for faster application logic and UI
development.

In the real data layer and networking implementation, there is a Android
service connected to the Scampi router application via the AppLib library. The
Scampi router is in turn connected the the network. This Service is
responsible for the whole communication with the network layer. On startup it
subscribes to messages sent to the `discovery` service and after receiving
Topics on this service, it dynamically subscribes to the services of this
Topics in order to recive Posts, PostExtensions and Votes. Furthermore, if the
Android app has permissions to acess data, it will receive the Extensions
needed to show certain PostTypes from the network. The Service is also
responsible for publishing messages to the network. To do this, the
repositories bind to the service, and call messages provided by this binding,
which then published messages to the network. Only after they have been
received again by this service, they will be inserted into the database and
therefore be shown in the UI. The Data and Network layer by desing don't know
anything about the data contained in posts and post extensions, which allows
quick modifications to the upper layers without modifing the lower layers.

# Extension System

To allow various Post Types without a need to modify the application, the
application has a built in extension system. These extensions allow to display
and create Posts and their corresponding PostExtensions. They are dynamically
loaded at runtime when the user interacts with Posts which reference their
extension UUID as their post type. If the Extension is updated it needs  to be
given a new UUID, which allows multiple versions of one Extension to coexist.

Extensions are built as Android Applications as those already contain the
`.dex` File needed by the Android runtime to load code. Furthermore, the
extension need integrate the localcampuslib android library as it provived the
interfaces used to interact with the host application. to A Extension needs to
atleast provide one Android Fragment responsible for creating and one Fragment
responsible for showing Posts. These Fragments need to be registered as static
fields  in the `Registry` class in the predefined package
`de.tum.localcampusextension`. This `Registry` furthermore needs to contain
the Extensions UUID and a description of the Extensions as static fields.

The Fragment to show a post, which needs to extend the `ShowPostFragment`
class provided by the localcampuslib, is instantiated and shown by the
ShowPost Activity depending on the Posts PostType. The ShowPost Activity
provides the Fragment with a Modiefied context which contains the assets and
resouces defined in the apk file (it only supports the assets defined by the
extension, the extensions has no acess on the host applications resources.
Furthermore, it provides it with a `ShowPostDataProvider` which allow the
Fragment to acess the Post and it's PostExtensions and add aditional post
extensions. As posts extensions contain e.g. a comment or a answer in a poll,
they need to be added from the view to show the post itself.

The fragment to create a Post, which needs to extend the `AddPostFragment`
class provided by the localcampuslib, is instantiated by the CreatePost
activity depending on the PostType selected before. The Activity again
provides a Content modified in the same way as it is for the ShowPost
Fragment. Furthermore, it provides a `AddPostDataProvider`, which allows the
Fragment to create a Post for to the selected Topic and PostType.

As the Context contains the resources from the extensions .apk file,
developing the extension should be similar to developing a regular Android
fragment, with the exception that xml resouces currently cannot acess or
reference other resources. Furthermore, the Layout inflater passed to
`onCreateView` cannot be used directly, instead it needs to be patched like this:
```java
LayoutInflater newInflator = inflater.cloneInContext(getContext());
```

This extension system currently contains multiple security problems, which
would need to be solved before running such an application in production. The
extensions system loads java code that is directly executed in the process of
the host application, therefore it could do anything that is possible in the
host application. Furthermore, the extension apk are stored on the external
storage of the device. While this allows easier adding of new extenions, the
extensions could be replaced with malicious code by other applications. To
secure this, the app would need to provide a way to verify extensions, for
example the app could only load extensions that are signed by authors
explicitly thrusted by the user. If this is not enough, the extension system
could be moved to sandboxes webviews.