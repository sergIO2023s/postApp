# PostApp

# Installation

```
gradlew assembleDebug
gradlew installDebug
```
Or just sync the gradle files and click on the green play button in Android Studio 
(with a connected device or an emulator created using the Device Manager Tool).

# Architecture

It is a domain-driven architecture. Even though the business layout doesn't have that much login, I design it that way thinking about how this kind of application could evolve over time. A business layer will be useful for isolating business logic and reducing the complexity of the `ViewModel`s since they already have a lot of UI logic to take care of.

The structure of the app applies reactive principles in some parts supported by `Flow` and Coroutines. They mostly run on the main thread, even asynchronous calls, given that, unlike threads, the suspending blocks don't affect the main thread performance that much if they are short tasks and that was the case, so not any other dispatchers were used, and the scope always was `ViewModelsScope` to guarantee cancelation according to the `ViewModel` lifecycle. The `Exceptions` and other controlled errors are passed from the data layer to the UI and each layer has the responsibility to map those error according to certain rules and decide if it needs to be shown to the user or just logged internally. I didn’t implement the error management for all the cases and all the layers but the implemented scheme shows how it could be managed and scaled by adding the corresponding mapper on each layer.

The fragment where the user can see the details has two sections. The user and post information are located in the first section and the comments (which have to be downloaded using another end-point) are in the second and they are downloaded independently. This reactive approach allows the user to see the information as soon as it is available, and each section reacts to the stream of items showing immediately its update state instead of downloading both of them, maybe merging the data and then showing it to the user.

The application has 3 layers and applies certain Clean Architecture principles. But, having into account the limited time, the extent, and the size of this test, it has some simplifications. It applies the dependency inversion pattern to isolate the repository from the business layer, but I decided to reuse some of the entities as immutable objects for the business layer to avoid a lot of mappers and boilerplate code that may be necessary for a real-world app to guarantee the appropriate dependency relationships. If the app were to scale, it will be convenient to isolate the layers completely, but of course having into account constraints (business requirement, time, resources, etc)

## Data 

It uses a repository, but it isn't using the repository pattern, (it doesn't have a Datasource abstraction) it is just a singleton object with the responsibility of bringing the data asked by the related usecase in the business layers from the API, the database and the shared preferences. It abstracts remote and local data management. It uses flow to emit asynchronously and update the database with the data downloaded from the API. It uses Room and Kotlin Flow to emit items when the database is updated. This design uses the database as the source of truth and each change is shown to the user immediately. If the app were bigger and some random changes were made in other sections of the view to a post using, for example, some asynchronous request (eg a request to the server) all the corresponding changes will be shown as soon as the server answer arrive independently of the user actions at that moment. This reactive approach is useful for showing the source of truth state as soon as possible and for isolating side effects since they are all managed as an emitted event by the flow coming from the database, but it has to be used with caution in real-world apps because it can generate problems if the UI state is not properly controlled, considering that one can't be sure when an event will be emitted.


## Domain

This will be (at least in theory because it depends on the app) the less updated layer over time, so it is convenient to have the `Repository` which it depends directly on completely isolated without any references to it.

It contains all the business logic. For actual production apps, I’ve found this layer very useful to avoid mixing UI login with business logic causing big and complex view models. In this case, it was not necessary (all of them are just short functions calling a repository function, and some mappers were omitted) but I used this approach assuming the app could grow over time.

## Presentation

### Viewmodels 

I used MVVM using Coroutines, `Flow`, and `Livedata` to deliver the state to the UI. The Viewmodels manage the views state without any references to them. It has a lot of advantages regarding maintainability and scalability and is very helpful for testability (I added a few unit tests to the `getPost()` function where using dependency injection as a complement made it easy to generate input and read the outputs without requiring any UI Android framework component or complex configuration or logic). It uses the observer pattern taking advantage of the Livedata class properties. It is lifecycle aware and was built to work in conjunction with `Viewmodels` to survive different lifecycle changes to the activities and fragments involved. There are other approaches using Flow to emit Ui changes to the views, but I have found that, with LiveData, lifecycle management is more clear, simple, and easy to use.

### UI 

I use an “activity with several fragments” structure using the Jetpack `Navigation` library. It simplifies fragment stack management and the use of shared data, as well as other useful features like navigation bar integration. I wanted to use pagination at first but the API just return 100 items and it doesn't provide enough features. Using MVVM and the Viewmodel as state containers, the `Fragment`s are very simple. They just render the state objects.

Considering that the data corresponding to a post's details are stored on demand instead of all at once and it also needs different end-points to get the information, the `Fragment` for showing those details gets the information from two different reactive streams of data, so it manages the post-user section and the comment section independently. 

The post list fragment (start point of the app) obtains the data mainly from the reactive stream coming from the updates to the source of truth, which is a sqlite table corresponding to the Post entity.

### Unit Tests 

I implement just I few tests. Since the business layer didn’t have interesting logic, I did not add unit tests for this layer. (PD: I forgot to clarify the tested function on each test. The name of the test files could cause some confusion since I only tested one function per test file (the ones with interesting logic and more important for the app main features). I tested the `getPost()` function on the `PostListViewModelTest` and the `getAllPost()` function on the `PostRepositoryTest`. It follows the convention `given_when_then`.


# PostApp (Features)

PostApp is an app with a very simple design that allows you to read posts online and offline.

### Loading new posts from the Server

Using the swipe top-down gesture until a loading icon is shown. If there are posts marked as favorites, they won't lose their state after reloading all the posts

### Offline Mode  

When you start the app for the first time it's going to try to download the posts. If it is not possible, it’ll show a Toast indicating there was an error. It could take up to 60 seconds because of the default timeout for server requests). But you can try to download again after a few seconds just using the swipe top-down gesture.

Once the posts are downloaded, the app will save them locally, so you can visit them without an internet connection.

When you go to the details section, all the details will be asked to the server and they will be stored on demand, so you can visit them again offline.

### Selecting a post:

Each row has three sections, title, content, and favorite marker. Tap on the content section to go to the details where you will be able to see the author's information and the comments.

### Favorite posts: 

If you want to add a post to your favorite, you just have to tap on the right side of the item. A star icon will appear indicating it‘s been marked as a favorite.

### Delete a post:

Use the drag gesture from left to right to delete the post from the posts list

### Delete all posts but favorites 

Use the floating button located in the bottom right corner. All the posts will be deleted with or without an internet connection. All the favorites won't be deleted.
