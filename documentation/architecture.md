# Local Campus App - Description of the Application

The initial goal of the application was to provide a local version of the
campus communication app [Jodel](https://jodel.com/), extended with additional
ways to interact with the environment (e.g. the cafeteria or the thermostat of
a room). In order to support various ways to interact with the environment,
instead of creating an application with a fixed number of interactions, we
created the application to support user created extensions that are shared
over the network. Instead of text and image posts as they exist in Jodel, the
feeds in our application consist of *Posts* that allow the user to interact
with the environment in various ways. The communication channels, which are
called *Topics* in our Application, are tiered to the location, and
interaction is only possible with Topics that are relevant to the users
location.

# System Architecture

Our system consists of Android devices with our application installed acting
as the users interface with our system and Raspberry Pi's acting as *Hubs* for
a specific location. They communicate over Scampi, which is a Delay Tolerant
Oppurtunistic Networking Middleware. For Scampi to work, each device has to
run the Scampi Router application. Scampi allows the devices to store and
carry messages with them and therefore share them with multiple Hubs. This is
important as some messages are relevant for the whole campus and therefore
need to be transfered between multiple Hubs/rooms. In our architecture, there
SHOULD be one hub for every distinct location, e.g. a room or the cafeteria.
The Android devices don't communicate directly, instead they only interact
with the Hubs. The Hubs also aren't connected in any way, they only receive
messages from the clients interacting with them.

In a first, basic version of the application, the Hubs act as wireless access
points which have the Scampi Router application running on a well known IP and
port. When available, they also share their wired ethernet connection with the
devices conncted to the wireless network. In a future version of the
application, the Hubs could be connected to the campus network, in which case
the client devices could stay connected to the more powerfull campus network
as well. The hubs would then need to be identified and connected to usign an
alternative method, one solution would be the broadcast their IP over
bluetooth beacons, which the Scampi Router application would then connect to.
Even if this additional method to connect to a hub is added, the basic
connectivity method should remain, as the application can be deployed
independently of the exisiting infrastructure.

# Entities / Message Types

Our application has multiple important entities.

## Topic 

The most general entity is a Topic, which defines a channel of communication,
e.g. `/tum` or `/tum/garching`. While there is a structure to this Topic
names, the system treats them independently of each other (the logic for
tiering could be added to the application layer, as it is done by the
publishing application), as each of the represents a Scampi Service, and
those services don't support any tiering. Topics are shared over the
`discovery` service, which is populated by a special publishing application
running on every Hub. They MUST not be shared by the android devices, they
should only come directly from the hub the client is connected to. The Topic
messages contain a field `deviceIdentifier`, which identiefies the location
the Topic was broadcasted at. As the app will receive the location identifer
of the Hub it is connected to using Scampi's location update service, this
allows the app to identify all Topics relevant to the current location.

```
String: topicName // e.g /tum or /tum/garching
String :deviceIdentifier // Identifier of the device (location)
```
Topic Message on the `discovery` service

## Post

The most important entity in our system is a *Post*. It corresponds to one
root message and is published to one Topic. It contains meta information like
the creation date and the id of the creator and the data, which is sent as a
string and parsed depending on the *PostType*. The data MUST be encoded as
JSON and contain the two mandatory fields `text` and `color`, which are used
to display the list of Posts. The rest of the data is defined by the PostType
and can therefore only be parse by the *Extension* responsible for this post
type.

```
String: message_type = post
String: topic // e.g. /tum
String: uuid
String: creator
Integer: created_at // timestamp
String: data // encoded as json, needs the to contain the fields "text" and "color"
String: type_id // PostRype, identifing the extension required to show this post (UUID)
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

Furthermore, there can be multiple *PostExtensions* attached to a Post,
which contain similar fields like the Post itself and are only parsed by the
extension responsible for the Posts PostType. A PostExtension could for
example be a Comment attached to a Text Post, which allows interaction similar
to the one provided by Jodel. The PostExtensions are also spread over the
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

The Extensions needed to show PostTypes that are not shipped with
the applicaion need to be shared between devices. This happens over simple
messages containing the Extension apk file and the Extensions UUID. If a
Extension needs be updated, it needs to be published with a new UUID, and
therefore be threated as a new Extension. The Extensions are shared over a
special `extensions` service.

```
String: message_type = extension
String: uuid
Binary: binary
```
PostExtension message on the `extensions` service

## General remark around the Entities

While at first we planned to update the Posts to include new PostExtensions
and Votes and merge them if there were concurent changes, which would lead to
a situation where the always is only one self stabelizing message per Post in the
system, we decided to use seperate messages for Votes and PostExtensions to
reduce the network overhead. Furthermore, updating messages on remote nodes is
not supported by the communication framework, therefore the outdated messages
would remain on the remote systems. Finally, in order to be able to merge
posts they would need to be designed in a special way (e.g. no hard deletes),
which would lead to complex messages and a generall complexity similar to the
one created by the need for seperate messages for those additions.

In order to support post types that contain complex binary data, another
entity type *BinaryPost* should be added to the system which is similar to the
regular Post Entity, but contains an additional binary field which can be used
to e.g. transfer an image. As this has different requirements to the device
(e.g. storage permissions / storage space), and in order to prevent backwards
compabilty issues, this should be treated as a new entity instead of updating
the existing entity. To allow devices to be able to decide wether they
want to receive binary Posts, they also have to be sent to an additional service.

# Android Application Architecture

For our Android app we decided to extensively use [Android
Jetpack Components](https://developer.android.com/jetpack/). 
Jetpack is a state of the art set of components, tools and guidance to 
create apps with modern Android features. Jetpack components are 
self-contained libraries, so they don’t have be combined with other Jetpack 
components necessarily. We used the following Jetpack components frequently:
- ViewModel
- LiveData
- Navigation

We decided not to use the Jetpack-Navigation component (which is the
recommended way to do navigation within the app), because it doesn't fit our
use-case of dynamically loaded Extensions to show different PostTypes. The
Navigation library transitions between multiple Fragments that need to be
defined at compile time and are hosted by a single Activity. As it is
impossible to reference the Fragments of dynamically loaded Extensions at
compile time, the Fragments to add or show Posts would need to be nested
within another Fragment that is known at compile time. This would lead to an
additional layer of nesting compared to our approach, where every view of the
app is an Activity and some of those activities host dynamically loaded
Fragments.

The Android application is structured in a way that every Post, PostExtension
and Vote shown in the UI has been received over the network, even the ones
created by the app itself, which leads to a reproducible data flow. The
application is encapsulated from the networking and data layer by an
repository layer, which ensures that this dataflow is respected. Furthermore,
the repository layer with it's well defined interface allows easy replacement
of the actual data implementation with a local, in- memory variant for faster
application logic and UI development.

The UML diagram shown in Figure 1 describes the components of our application
frontend and how they interact with each other.

![Frontend Architecture](FrontendArchitecture.png)

`MyFragment`, which is an Android Fragment, has some Android UI components
(e.g. a TextView to show a message), which are updated by calling the method
`updateView(String message)`. In order to encapsulate the data processing
logic (e.g. extracting a message from the Model) from the Fragment, our system
is designed according the MVVM pattern. In this pattern, the Fragments, which
represent the Views (V) in the Pattern, only show the data (`message`) without
having knowledge about how the Model (M) need to be processed. The processing
is done by the ViewModel (VM), which transforms the Models into the form it is
shown in within the View (In the Example, this step would be extracting the
Message text from the `MessageModel`). The ViewModel fetches this Model data
from the repository layer. Because all the processing (like parsing JSON data,
tranformations of LiveData, error handling etc.) is done in the ViewModel, it
can be tested without any slow GUI tests or Android instrumented unit tests.
This promotes testing of the frontend of the app.

The frontend uses [LiveData](https://developer.android.com/topic/libraries/a
rchitecture/livedata), which is an observable data holder. LiveData lets the
components of an app observe LiveData objects for changes to the contained
Data, and therefore use updated data without manually polling it. One benefit
of LiveData compared to other Reactive Frameworks is its lifecycle-awareness.
The LiveData objects respect the lifecycle state of the app’s components
observing them (Activities, Fragments). This ensures that the observers only
receive data updates when they are in an active lifecycle state. In the
example from the diagram application, the ViewModel returns LiveData
containing the message. `MyFragment` observes this LiveData and calls its
`updateView(String message)` method if the data is changed.

In the real implementation of th networking and data layer, there is an Android
Service connected to the Scampi Router application via the AppLib library.
The Scampi router is in turn connected the the network. This AndroidService is
responsible for the whole communication with the Scampi network. On startup it
subscribes to messages sent to the `discovery` service and after receiving
Topics relevant to the location, it dynamically subscribes to the services of
these Topics in order to recive Posts, PostExtensions and Votes. The Android
Service is also responsible for publishing messages to the network. To do
this, the repositories bind to it, and call methods provided by this binding,
which then publish the messages to the network. Only after they have been
received again they will be inserted into the database and therefore be shown
in the UI. The database is a sqllite database queried and managed by the [Room
ORM library](https://developer.android.com/topic/libraries/architecture/room).
Room already returns LiveData that will be transformed in the repositories and
viewmodels.

![Simplified diagram of the components involved in handling Posts](dataflow.png)

Figure 2 shows a simplified diagram of the components involved in interacting
with a Post. The method `getPostsforTopic(long topicId)` in the PostRepository
directly reads the data (as LiveData) from the Database. If the UI layer wants
to add a new Post using the `addPost(Post post)` method of the PostRepository,
the repository will call the ScampiPostSerializer (not shown in the Figure) to
serialize the message into a SCAMPIMessage, and publish that to the network
via it's binding to the AppLibService. As explained before, the AppLibService
subscribes to all Topics relevant to the Location. To do this, it attaches a
TopicHandler to the Topics service. If this Handler determines that a received
message contains a Post, it deserializes it using the ScampiPostSerializer
(not shown in the Figure) and calls the insert method of the
NetworkLayerPostRepository. This Repository handles duplicate inserts, manages
the relations and inserts the Post into the database using the Room DAOs. It
ensures that the Topic the Post belongs to exists and links any related
PostExtensions and Votes received before the Post to the Post, which ensures
fast read access as there are real SQL relations with numeric indicies. As the
data is inserted into the Database, the Database will trigger LiveData
updates, so if the GUI is observing the Posts for a Topic, it will receive
an update.

If the Android app has permissions to access local data, it will share the
Extensions needed to show certain PostTypes with the network. The data and
network layer by design don't know anything about the data contained in Posts
and PostExtensions, which allows quick modifications to the upper layers of
the application without changing the lower layers.

# Extension System

To allow various Post Types without a need to modify the application, the
application has a built in Extension System. These extensions allow to display
and create Posts and their corresponding PostExtensions. They are dynamically
loaded at runtime when the user interacts with a Posts referencing their
extension UUID as its post type. If the Extension is updated it needs to be
given a new UUID, which allows multiple versions of one Extension to coexist.

Extensions are built as Android Applications as those already contain the
`.dex` File needed by the Android runtime to load code. Furthermore, the
Extension need to integrate the `localcampuslib` Android library as it
provives the interface to interact with the host application. An Extension
needs to atleast provide one Android Fragment responsible for creating and one
Fragment responsible for showing Posts. These Fragments need to be registered
as static fields  in a `Registry` class in the predefined package
`de.tum.localcampusextension`. This `Registry` furthermore needs to contain
the Extensions UUID and a description of the Extensions as static fields.

The Fragment to show a Post, which needs to extend the `ShowPostFragment`
class provided by the localcampuslib, is instantiated and shown by the
`ShowPostActivity` depending on the Posts PostType. The ShowPostActivity
provides the Fragment with a modiefied Context, which contains the assets and
resouces defined in the apk file (it only supports the assets defined by the
Extension, the Extension has no access on the host applications resources.
Furthermore, it provides the Fragment with a `ShowPostDataProvider` which
allows the Fragment to access the Post and it's PostExtensions, upvote and
downvote it and add aditional PostExtensions. The DataProvider also allows the
extension to access the current userId to prevent the user from creating
duplicate Extensions. As this is only a local safety messure (you cannot
control the network), Extensions also need to take care of unwanted duplicate
PostExtensions when reading them from the DataProvider. For example, a voting
application could only use the latest vote of an user. In that case, duplicate
Votes could still exist in the network and the database, they are just ignored
by the Extension.

The fragment to create a Post, which needs to extend the `AddPostFragment`
class provided by the localcampuslib, is instantiated by the
`CreatePostActivity` depending on the PostType selected before. The Activity
provides a Content modified in the same way as it is for the
`ShowPostFragment`. Furthermore, it provides a `AddPostDataProvider`, which
allows the Fragment to create a Post for to the selected Topic and PostType.

As the Context contains the resources from the extensions .apk file,
developing the Extension should be similar to developing a regular Android
Fragment, with the exception that xml resouces currently cannot access or
reference other resources or classes provided by external packages.
Furthermore, the Layout inflater passed to `onCreateView` cannot be used
directly, instead it needs to be patched like this: 

```java
LayoutInflater newInflator = inflater.cloneInContext(getContext());
```

The components described above can also be found in the diagram shown in
Figure 3.

![Anathomy of a minimal Extension & the Extension interface](extensions.png)

This extension system currently has multiple security problems, which
need to be solved before running such an application in production. The
Extension System loads java code that is directly executed in the process of
the host application, therefore the Extension can do anything that is possible
in the host application. Furthermore, the Extension apks are stored on the
external storage of the device. While this allows easier adding of new
Extenions, the Extensions could be replaced with malicious code by other
applications. To secure this, the app needs to provide a way to verify the
integerity of Extensions. For example, the app could only load Extensions that
are signed by authors explicitly thrusted by the user. If this is not enough,
the Extension System could be modiefied to use sandboxes webviews instead of
Android Fragments.