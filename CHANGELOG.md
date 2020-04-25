#### 1.9.10-b6

- **[UPDATE]** Updated to Kotlin 1.3.72.
- **[UPDATE]** Updated to Dokka 0.10.1.
- **[CHANGE]** Javadocs are no longer generated with Dokka. Since KTX focuses solely on Kotlin support for LibGDX,
usability from Java is not a priority. The generated Javadocs are not very helpful, especially for Kotlin development.
Instead, the Javadoc jar published to Maven Central now contains exported Kotlin-compatible Dokka documentation.
Starting from this release, GitHub releases will no longer contain the Javadoc archives.
- **[FEATURE]** (`ktx-actors`) Added `Action.repeat` extension method that allows to repeat an action for the given amount of times.
- **[FEATURE]** (`ktx-ashley`) Added `Engine.get` operator to access a chosen `EntitySystem`.
- **[FEATURE]** (`ktx-ashley`) Added `Engine.getSystem` extension method to access a chosen `EntitySystem`. Throws `MissingEntitySystemException` in case the system is not added.
- **[FEATURE]** (`ktx-ashley`) Added `Entity.addComponent` extension method to create a `Component` for an existing `Entity`. 
- **[FEATURE]** (`ktx-ashley`) Added `Entity.plusAssign` (`+=`) operator that allows to add an `Component` to an `Entity`. 
- **[FEATURE]** (`ktx-ashley`) Added contracts support to `EngineEntity.with`, `Engine.create`, `Engine.add`, `Engine.entity`
add `Entity.addComponent`. Now their lambda parameters are ensured to be executed exactly once:
```kotlin
// Before:
lateinit var value: Int
engine.add {
  value = 42
}

// Now:
val value: Int
engine.add {
  value = 42
}
```
- **[FEATURE]** (`ktx-assets`) `Iterable.dispose` and `Array.dispose` extension methods consuming an error handler are now inlined.
- **[FEATURE]** (`ktx-box2d`) Added contracts support to body, fixture and joint factory methods, as well as `FixtureDef.filter`
This ensures that the configuration lambdas are executed exactly once.
- **[CHANGE]** (`ktx-collections`) `PooledList` was removed due to concurrent iteration safety issues. Use standard library lists instead.
- **[CHANGE]** (`ktx-collections`) `-` and `+` operators no longer mutate the collections. Instead, they create a new collection instance and add or removed the selected elements.
To modify an existing collection, use new mutating `+=` and `-=` operators.
- **[FEATURE]** (`ktx-freetype`) Added contracts support to `AssetManager.loadFreeTypeFont`, `freeTypeFontParameters`
and `FreeTypeFontGenerator.generateFont`. This ensures that the font configuration lambdas are executed exactly once.
- **[FEATURE]** (`ktx-freetype-async`) Added contracts support to `AssetStorage.loadFreeTypeFont`.
- **[FEATURE]** (`ktx-graphics`) Added contracts support to `Camera.update`, `Batch.use`, `ShaderProgram.use`, `GLFrameBuffer.use` and `ShapeRenderer.use`.
- **[FEATURE]** (`ktx-inject`) Added contracts support to `Context.register`.
- **[CHANGE]** (`ktx-log`) Added contracts to logging methods. Logging methods now might need to be imported explicitly.
- **[FEATURE]** (`ktx-preferences`) Added contracts support to `Preferences.flush`.
- **[FEATURE]** (`ktx-math`) Added `+=`, `+`, `-=` and `-` operators supporting floats and ints to `Vector2` and `Vector3`.
- **[CHANGE]** (`ktx-math`) `-`, `!`, `++` and `--` operators no longer mutate vectors and matrices, returning new instances instead.  
- **[FIX]** (`ktx-math`) Operators documentation regarding mutating of vectors and matrices was updated.
- **[FEATURE]** (`ktx-scene2d`) `scene2d` object was added. It supports the entire Scene2D DSL and allows to create root-level widgets.
- **[FEATURE]** (`ktx-scene2d`) `Stage.actors` extension method was added. It allows to define actors with Scene2D DSL and adds all top-level actors to the `Stage`.
- **[CHANGE]** (`ktx-scene2d`) Root-level `actor` function was deprecated.
- **[CHANGE]** (`ktx-scene2d`) Root-level widget factory functions were deprecated. Use `scene2d.` prefix or `Stage.actors` to create these widgets.
Note that the actors can still be created via standard DSL. See the migration guide in README. This includes:
  - `stack`
  - `horizontalGroup`
  - `verticalGroup`
  - `container`
  - `splitPane`
  - `scrollPane`
  - `table`
  - `window`
  - `dialog`
  - `buttonGroup`
  - `tree`
- **[CHANGE]** (`ktx-scene2d`) `listWidget` and `selectBox` now have a single generic type to improve usability.
Their building blocks no longer consume `Cell` and `Node` instances.
- **[CHANGE]** (`ktx-scene2d`) Internal `KWidget.appendActor` and `KGroup.add` methods was removed.
- **[FEATURE]** (`ktx-scene2d`) Added contracts support to widget factory methods and `Stage.actors`.
This ensures that widget configuration lambdas are executed exactly once.
- **[FEATURE]** (`ktx-style`) Added contracts support to style factory methods and top-level `skin` functions.
- **[CHANGE]** (`ktx-vis`) Overhaul of the module.
  - `ktx-vis` now includes and extends the `ktx-scene2d` module. The majority of APIs are now shared.
  - All factory methods for VisUI widgets are now inlined, which can improve the performance of GUI building.
  - Factory methods of some VisUI widgets were renamed to avoid clashes with Scene2D methods and better reflect the wrapped widget class names:
    - `label`: `visLabel`
    - `image`: `visImage`
    - `list`: `visList`, `visListOf`
    - `selectBox`: `visSelectBox`, `visSelectBoxOf`
    - `slider`: `visSlider`
    - `textArea`: `visTextArea`
    - `textField`: `visTextField`
    - `validatableTextField`: `visValidatableTextField`
    - `textButton`: `visTextButton`
    - `imageButton`: `visImageButton`
    - `imageTextButton`: `visImageTextButton`
    - `radioButton`: `visRadioButton`
    - `tree`: `visTree`
    - `table`: `visTable`
    - `scrollPane`: `visScrollPane`
    - `splitPane`: `visSplitPane`
    - `addTooltip`: `visTooltip`
    - `addTextTooltip`: `visTextTooltip`
  - Parental actors including `collapsible`, `dragPane`, `horizontalCollapsible`, `visScrollPane`, `visSplitPane` and
  `multiSplitPane` now do not require passing widgets to their factory methods. Instead, widgets are either automatically
  created or can be defined as nested children with the same DSL.
  - Inlined functions with lambda parameters, such as widget factories with their building block lambdas, now use
  Kotlin contracts to ensure that they are executed exactly once.
  - `DEFAULT_STYLE` constant is removed in favor of `defaultStyle` from `ktx-scene2d`.
  - `styleName` parameters in factory methods were renamed to `style` for consistency with `ktx-scene2d`.
  - `@VisDsl` DSL marker is replaced with `@Scene2dDsl` marker from `ktx-scene2d`.
  - The sources documentation was greatly expanded.
- **[FEATURE]** (`ktx-vis-style`) Added contracts support to widget style factory methods.

Known issues:

- **[BUG]** (`ktx-box2d`) Due to a Kotlin compiler bug, methods with _vararg_ parameters do not support contracts.
This includes some `polygon`, `chain` and `loop` factory methods. See [this issue](https://youtrack.jetbrains.com/issue/KT-30497).
They can still be used and work as expected, but the compiler does not ensure that their lambda parameters are executed exactly once.

#### 1.9.10-b5

- **[UPDATE]** Updated to Kotlin 1.3.71.
- **[UPDATE]** Updated to Kotlin Coroutines 1.3.5.
- **[UPDATE]** Updated to Gradle 5.6.4.
- **[FEATURE]** (`ktx-app`) Added profiling utilities.
    - `profile` inlined function allows to profile an operation with the LibGDX `PerformanceCounter`.
    - `PerformanceCounter.profile` inlined extension method eases usage of `PerformanceCounter` API.
    - `PerformanceCounter.prettyPrint` allows to print basic performance data after profiling.
- **[CHANGE]** (`ktx-app`) `LetterboxingViewport` moved from `ktx-app` to `ktx-graphics`.
- **[FEATURE]** (`ktx-ashley`) Added `Entity.contains` (`in` operator) that checks if an `Entity` has a `Component`.
- **[FEATURE]** (`ktx-assets-async`) Added a new KTX module: coroutines-based asset loading.
    - `AssetStorage` is a non-blocking coroutines-based alternative to LibGDX `AssetManager`.
         - `get` operator obtains an asset from the storage or throws a `MissingAssetException`.
         - `getOrNull` obtains an asset from the storage or return `null` if the asset is unavailable.
         - `getAsync` obtains a reference to the asset from the storage as `Deferred`.
         - `load` suspends a coroutine until an asset is loaded and returns its instance.
         - `loadAsync` schedules asynchronous loading of an asset.
         - `loadSync` blocks the thread until selected asset is loaded.
         - `unload` schedules asynchronous unloading of an asset.
         - `add` allows to manually add a loaded asset to `AssetManager`.
         - `dispose` unloads all assets from the storage.
         - `getLoader` and `setLoader` manage `AssetLoader` instances used to load assets.
         - `isLoaded` checks if loading of an asset was finished.
         - `contains` operator checks if the asset was scheduled for loading or added to the storage.
         - `progress` allows to check asset loading progress.
         - `getReferenceCount` returns how many times the asset was loaded or referenced by other assets as a dependency.
         - `getDependencies` returns a list of dependencies of the selected asset.
         - `getAssetDescriptor` creates an `AssetDescriptor` with loading data for the selected asset.
         - `getIdentifier` creates an `Identifier` uniquely pointing to an asset of selected type and file path.
    - `Identifier` data class added as an utility to uniquely identify assets by their type and path.
         - `Identifier.toAssetDescriptor` allows to convert an `Identifier` to an `AssetDescriptor`. 
    - `AssetDescriptor.toIdentifier` allows to convert an `AssetDescriptor` to `Identifier` used to uniquely identify `AssetStorage` assets.
    - `LoadingProgress` is an internal class used by the `AssetStorage` to track loading progress.
- **[FEATURE]** (`ktx-async`) Added `RenderingScope` factory function for custom scopes using rendering thread dispatcher.
- **[FEATURE]** (`ktx-async`) `newAsyncContext` and `newSingleThreadAsyncContext` now support `threadName` parameter
that allows to set thread name pattern of `AsyncExecutor` threads.
- **[FIX]** (`ktx-async`) `isOnRenderingThread` now behaves consistently regardless of launching coroutine context.
- **[FEATURE]** (`ktx-freetype-async`) This KTX module is now restored and updated to the new `AssetStorage` API.
There are no public API changes since the last released version.
- **[FEATURE]** (`ktx-graphics`) Added `LetterboxingViewport` from `ktx-app`.
- **[FEATURE]** (`ktx-graphics`) Added `takeScreenshot` utility function that allows to save a screenshot of the application.
- **[FEATURE]** (`ktx-graphics`) Added `BitmapFont.center` extension method that allows to center text on an object.
- **[FEATURE]** (`ktx-graphics`) Added `Camera` utilities.
    - `center` extension method allows to center the camera's position to screen center or the center of the chosen rectangle.
    - `moveTo` extension method allows to move the camera immediately at the chosen target position with optional offset.
    - `lerpTo` extension method allows to move the camera smoothly to the chosen target position with optional offset.
    - `update` inlined extension method allows to change camera state with automatic `Camera.update` call.
- **[FEATURE]** (`ktx-math`) Added `lerp` and `interpolate` extension functions for `Float` ranges.
- **[FEATURE]** (`ktx-preferences`) Added a new KTX module: Preferences API extensions.
    - Added `set` operators for `String`, `Int`, `Float`, `Double`, `Long`, `Boolean`, `Pair<String, Any>` and `Any`
    - Added `get` operator which automatically determines preference type and retrieves them with the correct method.
    - `get` and `set` will automatically attempt to (de)serialize non-basic preferences to and from JSON.
    - `set(String, Double)` is deprecated, since the LibGDX `Preferences` do not support doubles.
    - Added `flush` inlined extension method that executes a lambda and automatically calls `Preferences.flush`.
- **[CHANGE]** (`ktx-scene2d`) Improved typing support for `Tree.Node` widgets. Since LibGDX 1.9.10, `Tree.Node` is
a generic class, but KTX `KNode` remained non-generic until now. Type of stored actors must now be specified for `KNode`
variables, but thanks to that actors from `KNode` instances are now correctly typed and easier to handle. This required
minor internal changes - `KWidget.storeActor` is now generic.
- **[FEATURE]** (`ktx-vis`) Added `image` (`VisImage`) factory methods consuming `Texture`, `TextureRegion` and `NinePatch`.

#### 1.9.10-b4

- **[FEATURE]** (`ktx-actors`) Added `onTouchDown`, `onTouchUp` and `onTouchEvent` extension methods that allow to attach `ClickListener` instances to actors.
- **[CHANGE]** (`ktx-collections`) `Array.removeAll` and `retainAll` now return a boolean if any elements were removed.
- **[CHANGE]** (`ktx-collections`) `Array.transfer` is now less strict about typing.
- **[FEATURE]** (`ktx-math`) Added Kotlin ranges extensions that simplify creating ranges and rolling random numbers:
    - `Int.amid`, `Float.amid`;
    - `+`, `-`, `*` and `/` for ranges;
    - `ClosedRange<Float>.random`, `IntRange.random`;
    - `ClosedRange<Float>.randomGaussian`;
    - `ClosedRange<Float>.randomTriangular`.
- **[FEATURE]** (`ktx-tiled`) Added a new KTX module: Tiled API extensions.
  - Added `contains` (`in`) and `set` (`[]`) operators support to `MapProperties`.
  - Added extension methods that simplify properties extraction from `MapLayer`, `MapObject`, `TiledMap`, `TiledMapTile` and `TiledMapTileSet`:
    - `property`
    - `propertyOrNull`
    - `containsProperty`
  - Added `shape` extension field to `MapObject`.
  - Added extension fields that ease extraction of basic properties from `TiledMap` and `MapObject`.

#### 1.9.10-b3

- **[UPDATE]** Updated to Kotlin 1.3.61.
- **[UPDATE]** Updated to Kotlin Coroutines 1.3.3.
- **[FEATURE]** (`ktx-assets`) Added `AssetGroup` abstract class that allows to manage groups of assets.
- **[FEATURE]** (`ktx-collections`) Added `removeAll`, `retainAll` and `transfer` extensions to LibGDX `Array` using lambda predicates to modify the array in-place.
- **[CHANGE]** (`ktx-collections`) `PooledList` now implements `MutableIterable`.
- **[FEATURE]** (`ktx-graphics`) Added `Batch.begin` extension methods that automatically set projection matrix from a `Camera` or `Matrix4`. 
- **[FEATURE]** (`ktx-style`) Added `Skin` extension methods with reified resource types: `optional`, `add`, `remove`, `has` and `getAll`.
- **[FEATURE]** (`ktx-style`) The overloaded `+=` operator can now be used to add `"default"` resources to `Skin`.
- **[FEATURE]** (`ktx-json`) Added `JsonSerializer` and `ReadOnlyJsonSerializer` adapters to facilitate writing custom serializers.
- **[FEATURE]** (`ktx-json`) Added `readOnlySerializer()` factory functions to simplify creation of `ReadOnlyJsonSerializer`.

#### 1.9.10-b2

- **[UPDATE]** Updated to Kotlin 1.3.50.
- **[UPDATE]** Updated to Kotlin Coroutines 1.3.0.
- **[UPDATE]** Updated to Gradle 5.6.1.
- **[CHANGE]** (`ktx-actors`) `Action.parallelTo` changed to `Action.along`.
- **[CHANGE]** (`ktx-actors`) `Action.along` (formerly `Action.parallelTo`) and `Action.then` no longer unwrap the second action.
- **[CHANGE]** (`ktx-actors`)`ParallelAction.along` (formerly `ParallelAction.parallelTo`) and `SequenceAction.then` simply add the second action to the group without unwrapping.
- **[FEATURE]** (`ktx-actors`) Added `/` operator to `Action`, which performs the non-mutating version of `along`, wrapping the caller and argument in a new `ParallelAction`.
- **[CHANGE]** (`ktx-actors`) `ParallelAction.plus()` and `SequenceAction.plus()` no longer unwrap their components.
- **[FIX]** (`ktx-actors`) `along`, `then`, `+` and `/` `Action` extension methods now properly differentiate between `SequenceAction` and `ParallelAction`, taking their inheritance into account.
- **[CHANGE]** (`ktx-box2d`) Added `disposeOfShape` parameters to `fixture` extension methods of `Body` and `BodyDefinition`. Setting these values to `true` will cause the fixture shapes to be immediately disposed of after `Fixture` construction.
- **[FIX]** (`ktx-box2d`) Removed memory leak caused by undisposed shapes.
- **[FEATURE]** (`ktx-graphics`) `Batch.use` extension methods now accept `Matrix4` and `Camera` to update the batch's projection matrix before rendering.

#### 1.9.10-b1

- **[UPDATE]** Updated LibGDX to 1.9.10.
- **[UPDATE]** Updated to Kotlin 1.3.41.
- **[UPDATE]** Updated to Kotlin Coroutines 1.3.0-RC2.
- **[UPDATE]** Updated VisUI to 1.4.4.
- **[UPDATE]** Updated to Gradle 5.5.1.
- **[CHANGE]** (`ktx-app`) `clearScreen` now also clears depth buffer to support 3D applications.
- **[FEATURE]** (`ktx-json`) Added a new KTX module with the goal of improving LibGDX `Json` API: `ktx-json`. The following extension methods were added to avoid passing Java class instances:
    - `fromJson`
    - `addClassTag`
    - `getTag`
    - `setElementType`
    - `setSerializer`
    - `readValue`
- **[FEATURE]** (`ktx-graphics`) Added `GLFrameBuffer.use` to allow safe omission of the `begin()` and `end()` calls.

#### 1.9.9-b2

- **[UPDATE]** Updated to Kotlin 1.3.31.
- **[UPDATE]** Updated to Kotlin Coroutines 1.2.1.
- **[FEATURE]** (`ktx-assets`) Added `TextAssetLoader` that can be registered in an `AssetManager` to load text files asynchronously.
- **[FEATURE]** (`ktx-style`) Added `Skin.get` extension method that allows to pass enum instances as style names.
- **[CHANGE]** (`ktx-style`) `Skin.get` extension method are no longer infix.
- **[CHANGE]** (`ktx-style`) `Skin.get` now has accepts default parameter equal to the default style name.

#### 1.9.9-b1

- **[UPDATE]** Updated LibGDX to 1.9.9.
- **[UPDATE]** Updated to Kotlin 1.3.20.
- **[UPDATE]** Updated to Kotlin Coroutines 1.1.1.
- **[UPDATE]** Updated VisUI to 1.4.2.
- **[UPDATE]** Updated to Gradle 5.0.
- **[CHANGE]** (`ktx-actors`) Replaced `Stage` and `Group` extension operator methods `plus` and `minus` 
with `plusAssign` and `minusAssign` to avoid mutating the objects with addition and subtraction operators.

```kotlin
// Adding an actor to a Stage/Group - before:
stage + actor
group + actor

// Now:
stage += actor
group += actor
```

- **[CHANGE]** (`ktx-actors`) Replaced `Stage` and `Actor` extension operator methods `plus` and `minus` 
with `plusAssign` and `minusAssign` to avoid mutating the objects with addition and subtraction operators.

```kotlin
// Adding an action to a Stage/Actor - before:
stage + action
actor + action

// Now:
stage += action
actor += action
```

- **[CHANGE]** (`ktx-actors`) `SequenceAction.then` was removed. Using the method on a sequence no longer mutates
it for consistency with `Action.then`. `then` now unwraps actors from passed `SequenceActions`.
- **[CHANGE]** (`ktx-actors`) `ParallelAction.parallelTo` was removed. Using the method on a `ParallelAction` no longer
mutates it for consistency with `Action.parallelTo`. `parallelTo` now unwraps actors from passed `ParallelActions`.
- **[CHANGE]** (`ktx-async`) Overhaul of the `ktx-async` module.
    - `KtxAsync` is now the main coroutines scope that should be used instead of the `GlobalScope`.
    - `Dispatchers.KTX` can be used to access a coroutines dispatcher that executes tasks on the main rendering thread.
    - `AsyncExecutorDispatcher` can be used to wrap LibGDX `AsyncExecutor` to execute tasks asynchronously.
    - `newSingleThreadAsyncContext` allows to create an `AsyncExecutorDispatcher` with a single thread.
    - `newAsyncContext` allows to create an `AsyncExecutorDispatcher` with the given max amount of threads.
    - `onRenderingThread` suspends the coroutine to execute a task on the main rendering thread and return its result.
    - `isOnRenderingThread` allows to check whether the corouting is executed on the main rendering thread.
    - `skipFrame` attempts to suspend the coroutine for at least one rendering frame.
    - `httpRequest` allows to perform an asynchronous HTTP request.
    - `schedule` and `interval` functions simplify LibGDX `Timer` API usage.
    - `AssetStorage` and associated asset loading utilities were temporarily removed. They will be added to a separate module.
    - Asynchronous tests were **significantly** simplified.
    - Assume that other utilities were either removed or integrated with the listed utilities.
- **[CHANGE]** (`ktx-freetype-async`) The module is temporarily disabled due to the removal of `AssetStorage`.
- **[FEATURE]** (`ktx-actors`) Added `+` operator to create sequence of actions (an alternative syntax to `then`).
- **[FEATURE]** (`ktx-actors`) Added `+=` operators to `SequenceAction` and `ParallelAction` to ease adding new actions to these action groups.
- **[FEATURE]** (`ktx-actors`) Added `stage` factory method that uses named and default parameters to ease `Stage` creation.
- **[FEATURE]** (`ktx-graphics`) Added `ShapeRenderer.use` to allow safe omission of the `begin()` and `end()` calls.
- **[FEATURE]** (`ktx-math`) Added `ImmutableVector2`, an immutable equivalent to `Vector2`.

#### 1.9.8-b5

- **[UPDATE]** Updated to Kotlin 1.2.70.
- **[UPDATE]** Updated to Kotlin Coroutines 0.26.1.
- **[UPDATE]** Updated to Gradle 4.10.2.
- **[FIX]** (`ktx-ashley`) Component classes without a default no-arg constructors could not have been initiated
by the Ashley engine. This is still the case, but now an exception with a meaningful message is thrown.

#### 1.9.8-b4

- **[UPDATE]** Updated to Kotlin 1.2.51.
- **[UPDATE]** Updated to Kotlin Coroutines 0.24.0.
- **[UPDATE]** Updated to Dokka 0.9.17.
- **[UPDATE]** Updated to Gradle 4.9.
- **[CHANGE]** (`ktx-async`) `KtxAsync.asynchronous` is now inlined. The action lambda is cross-inlined to avoid excessive object creation.

#### 1.9.8-b3

- **[UPDATE]** Updated to Kotlin 1.2.41.
- **[UPDATE]** Updated to Gradle 4.7.
- **[FEATURE]** (`ktx-graphics`) Added new graphics module with the following `ShapeRenderer` extension methods:
  - `arc`
  - `box`
  - `circle`
  - `cone`
  - `ellipse`
  - `rect`
  - `rectLine`
  - `rotate`
  - `scale`
  - `translate`
  - `triangle`
- **[CHANGE]** (`ktx-app`, `ktx-graphics`) Utility functions moved from `ktx-app` to the new `ktx-graphics`:
  - `color`
  - `Color.copy`
  - `Batch.use`
  - `ShaderProgram.use`
- **[MISC]** Removed migration guides from very old versions. If you are in a process of migrating an existing
application to the latest KTX and facing any errors, see `README` files in `1.9.8-b2` tag.

#### 1.9.8-b2

- **[UPDATE]** Updated to Kotlin 1.2.30.
- **[UPDATE]** Updated to Kotlin Coroutines 0.22.5.
- **[UPDATE]** Updated to Dokka 0.9.16.
- **[UPDATE]** Updated to Gradle 4.6.
- **[FEATURE]** (`ktx-math`) `dot` and `x` infix functions added to `Vector2` and `Vector3` allow to calculate dot
products and cross products of two vectors respectively.
- **[FEATURE]** (`ktx-box2d`) Initiation blocks of `Body` in `World.body` extension method is now optional thanks to
default lambda parameters in inlined functions.
- **[FEATURE]** (`ktx-box2d`)  `World.query` extension method allowing to execute AABB query with idiomatic Kotlin.
- **[CHANGE]** (`ktx-math`) Binary operators of `Vector2`, `Vector3`, `Matrix3` and `Matrix4` (`+`, `-`, `*`, `/`) no
longer modify the first vector or matrix. Instead, they create new instances of vectors or matrices that store the
operation result. Use the assign operators (`+=`, `-=`, `*=`, `/=`) instead to avoid creating new instances.
- **[CHANGE]** (`ktx-math`) New mutating assign operators (`+=`, `-=`, `*=`, `/=`) were added to `Vector2`, `Vector3`,
`Matrix3` and `Matrix4`.
- **[CHANGE]** (`ktx-math`) Parameters of matrix vector multiplication operators are switched. `vector * matrix` does
not exist anymore and now is available as `matrix * vector`.
- **[CHANGE]** (`ktx-math`) Operators of `Matrix3` to left-multiply a `Vector3` were removed.

#### 1.9.8-b1

- **[UPDATE]** Updated to LibGDX 1.9.8.
- **[UPDATE]** Updated to Kotlin 1.2.21.
- **[UPDATE]** Updated to Kotlin Coroutines 0.22.
- **[UPDATE]** Updated to Gradle 4.4.
- **[UPDATE]** Updated to VisUI 1.4.0.
- **[CHANGE]** (`ktx-scene2d`) Duplicate functions in Scene2D building DSL were removed thanks to optional default
lambda parameters in inlined functions (added in Kotlin 1.2). Due to the limitation in inlined methods, there used to be
two inlined methods for each actor to support syntax both with braces (init block) and without. Now there is just one
factory method supporting both syntax variants per widget. This should not affect most application, but might require
Kotlin 1.2 usage.
- **[CHANGE]** (`ktx-ashley`) Default functional parameters were added to `create`, `entity` and `with`, simplifying
the implementation and making configuration blocks optional.
- **[CHANGE]** (`ktx-inject`) Parameters of `bindSingleton` consuming multiple classes have been swapped to be more
compatible with the `bind` functions.
- **[CHANGE]** (`ktx-inject`) `bind` and `bindSingleton` methods consuming multiple classes now take `KClass` as
parameters instead of `Class`, so now you can use `YourType::class` instead of more verbose `YourType::class.java`.
- **[FEATURE]** (`ktx-style`) Initiation blocks of `Skin` and Scene2D actor styles are now optional.
- **[FEATURE]** (`ktx-vis-style`) Initiation blocks of VisUI actor styles are now optional.
- **[FEATURE]** (`ktx-box2d`) Initiation blocks of fixtures and joints are now optional thanks to default lambda
parameters in inlined functions.
- **[FEATURE]** (`ktx-inject`) Add higher-order function parameters for `bindSingleton` to allow the use of lambda
expressions.

```kotlin
fun createCircle(body: Body) {
    // Before - would not compile without additional braces:
    body.circle(radius = 2f) {}
    
    // Now - braces are optional (lambda parameter defaults to no-op):
    body.circle(radius = 2f)
}
```
- **[FEATURE]** (`ktx-freetype`) Implemented `ktx-freetype` module.
  - `AssetManager.registerFreeTypeFontLoaders` allows to register all loaders necessary to handle FreeType font assets.
  - `AssetManager.loadFreeTypeFont` provides Kotlin DSL for loading of FreeType fonts.
  - `freeTypeFontParameters` function provides Kotlin DSL for building FreeType font loading parameters.
  - `FreeTypeFontGenerator.generateFont` extension function allows to generate `BitmapFont` with Kotlin DSL.
- **[FEATURE]** (`ktx-freetype-async`) Implemented `ktx-freetype-async` module.
  - `AssetStorage.registerFreeTypeFontLoaders` allows to register all loaders necessary to handle FreeType font assets.
  - `AssetStorage.loadFreeTypeFont` provides Kotlin DSL for asynchronous loading of FreeType fonts.
- **[FIX]** (`ktx-box2d`) As LibGDX 1.9.8 fixes its `ChainShape` implementation, `ChainShape` utilities are supported
once again.

#### 1.9.7-b1

- **[UPDATE]** Updated LibGDX to 1.9.7.
- **[UPDATE]** Updated to Kotlin 1.1.51.
- **[UPDATE]** Updated to Kotlin Coroutines 0.19.3.
- **[UPDATE]** Updated to Gradle 4.3.
- **[BUG]** (`ktx-box2d`) `ChainShape` does not work correctly in LibGDX 1.9.7, and hence is not supported in KTX.
This might break existing applications.

#### 1.9.6-b7

- **[UPDATE]** Updated to Kotlin 1.1.3-2.
- **[UPDATE]** Updated to Kotlin Coroutines 0.17.
- **[UPDATE]** Updated to Gradle 4.0.2.
- **[CHANGE]** (`ktx-app`) `KotlinApplication` was removed. Use `KtxApplicationAdapter` or `KtxGame` instead.
- **[CHANGE]** (`ktx-app`) `KtxGame` no longer supports fixed rendering time steps.
- **[FEATURE]** (`ktx-app`) Clearing screen on rendering is now optional when using `KtxGame`. Change `clearScreen` parameter to `false` to turn off screen clearing.
- **[FEATURE]** (`ktx-box2d`) `World.rayCast` extension methods that allow creating ray-cast callbacks with the Kotlin
lambda syntax. `KtxRayCastCallback` alias added to ease implementation of this utility.
- **[FEATURE]** (`ktx-box2d`) Added `RayCast` object with constants that can be returned by the custom `RayCastCallback` implementations.

#### 1.9.6-b6

- **[UPDATE]** Updated to Gradle 4.0.
- **[UPDATE]** Updated to Ashley 1.7.3.
- **[CHANGE]** (`ktx-ashley`) Extensions updated to support `Engine` base class additionally to the `PooledEngine`.
  - `Engine.add` and `Engine.entity` extension methods to replace `PooledEngine` equivalents.
  - Changed `PooledEntity` to `EngineEntity`, wrapping `Entity` and providing access to `Engine` API.
- **[CHANGE]** (`ktx-async`) `TextAssetLoader` now extends `AsynchronousAssetLoader` instead of `SynchronousAssetLoader`.
- **[FIX]** (`ktx-async`) `AssetStorage` now correctly handles `SynchronousAssetLoader` instances on the main rendering thread.

#### 1.9.6-b5

- **[UPDATE]** Updated to Kotlin 1.1.2-5.
- **[UPDATE]** Updated to Kotlin Coroutines 0.16.
- **[FEATURE]** (`ktx-actors`) `onChange`, `onClick`, `onKey`, `onKeyDown`, `onKeyUp`, `onScrollFocus` and `onKeyboardFocus`
factory methods for `EventListener` instances were added. Contrary to existing factory methods, these use minimal set
of parameters to make listeners creation as concise as possible.
- **[CHANGE]** (`ktx-actors`) Existing `onChange`, `onClick`, `onKey`, `onKeyDown`, `onKeyUp`, `onScrollFocus` and
`onKeyboardFocus` factory methods where renamed to `onChangeEvent`, `onClickEvent`, `onKeyEvent`, `onKeyDownEvent`,
`onKeyUpEvent`, `onScrollFocusEvent` and `onKeyboardFocusEvent` respectively. Their excessive amount of parameters,
useful only on rare occasions, led to unnecessary boilerplate during listeners creation. See `ktx-actors` file
documentation for migration guide.
- **[FEATURE]** (`ktx-ashley`) new **KTX** module with Ashley entity component system utilities: `ktx-ashley`.
  - `PooledEngine.add` and `PooledEngine.entity` extension methods.
  - `PooledEntity` wrapping `Entity` and providing access to `PooledEngine` API.
  - `mapperFor` factory method that allows to create `ComponentMapper` instances.
  - Accessors for `Entity` objects using `ComponentMappers`: `get`, `has`, `hasNot`, `remove`.
  - DSL methods for constructing `Family` builders with `KClass` instances: `oneOf`, `allOf`, `exclude`.

#### 1.9.6-b4

- **[FEATURE]** (`ktx-app`) Added `KtxGame`: **KTX** equivalent of LibGDX `Game`.
- **[FEATURE]** (`ktx-app`) Added `KtxScreen`: adapter of the LibGDX `Screen` interface making all methods optional to implement.
- **[FEATURE]** (`ktx-app`) Added `emptyScreen` utility method returning a no-op implementation of `Screen`.
- **[FEATURE]** (`ktx-collections`) Added `map`, `filter` and `flatten` extension methods that return LibGDX collections.
- **[FEATURE]** (`ktx-collections`) `PooledList` now properly implements `hashCode` and `equals`.
- **[FEATURE]** (`ktx-inject`) `Context` now implements `Disposable` and allows to dispose of all registered singletons and providers.
- **[FEATURE]** (`ktx-inject`) Added `Context.remove` and `removeProvider` methods. Now providers for particular types can be removed without clearing the whole context.
- **[FEATURE]** (`ktx-inject`) `getProvider`, `setProvider` and `clear` methods of `Context` are now open and can be overridden.

#### 1.9.6-b3

- **[UPDATE]** Updated to Kotlin 1.1.2-3.
- **[UPDATE]** Updated to Kotlin Coroutines 0.15.
- **[CHANGE]** (`ktx-assets`) Static `AssetManager` instance container - `Assets` - was removed. All top level functions
depending on the global `AssetManager` were removed.
- **[FEATURE]** (`ktx-assets`) Added `FileType.getResolver` extension method creating `FileHandleResolver` instances.
- **[FEATURE]** (`ktx-assets`) Added `FileHandleResolver.withPrefix` extension method decorating resolvers with `PrefixFileHandleResolver`.
- **[FEATURE]** (`ktx-assets`) Added `FileHandleResolver.forResolutions` extension method decorating resolvers with `ResolutionFileResolver`.
- **[FEATURE]** (`ktx-assets`) Added `resolution` function constructing `ResolutionFileResolver.Resolution` instances.
- **[FEATURE]** (`ktx-async`) Added `AssetStorage`: a lightweight coroutines-based alternative to `AssetManager`.
- **[FEATURE]** (`ktx-box2d`) Implemented a new **KTX** module with Box2D physics engine utilities: `ktx-box2d`.
  - `world` factory method constructing `World` instances.
  - `World.body` extension method providing type-safe builder DSL for `Body` instances.
  - `FixtureDef` builder methods supporting all shapes (`CircleShape`, `PolygonShape`, `ChainShape`, `EdgeShape`).
  - `FixtureDef.filder` extension methods simplifying `Filter` properties setup.
  - `BodyDefinition` is a `BodyDef` extension providing `Body` building DSL. Used internally by `World.body`.
  - `FixtureDefinition` is a `FixtureDef` extension providing `Fixture` building DSL. Used internally by `BodyDefinition`.
  - `fixture`, `circle`, `box`, `polygon`, `chain`, `loop` and `edge` extension `Fixture` building methods added to `Body`.
  - `earthGravity` property allowing to set `World` gravity roughly matching Earth's gravity.
  - `onCreate` callbacks in `BodyDefinition` and `FixtureDefinition` giving access to built `Body` and `Fixture` instances in building blocks.
  - `Body` extension methods that ease creation of `Joint` instances between 2 bodies: `jointWith`, `gearJointWith`,
    `ropeJointWith`, `weldJointWith`, `motorJointWith`, `mouseJointWith`, `wheelJointWith`, `pulleyJointWith`,
    `distanceJointWith`, `frictionJointWith`, `revoluteJointWith`, `prismaticJointWith`.
- **[CHANGE]** (`ktx-i18n`) Static `I18NBundle` instance container - `I18n` - was removed.
- **[CHANGE]** (`ktx-i18n`) Top level `nls` functions were removed.
- **[FEATURE]** (`ktx-i18n`) `nls` property and method added to `BundleLine` for extra readability.
- **[CHANGE]** (`ktx-inject`) Static `Context` instance container was removed. All top level functions depending on the
global `Context` were removed.
- **[FEATURE]** (`ktx-inject`) `Context.register` builder method added to ease context initiation process.

#### 1.9.6-b2

- **[UPDATE]** Updated to Kotlin 1.1.1.
- **[UPDATE]** Updated to VisUI 1.3.0.
- **[FEATURE]** (`ktx-actors`) Added inlined `txt` extension properties to `Label` and `TextButton` widgets.
- **[FEATURE]** (`ktx-actors`) Added `KtxInputListener`: an `InputListener` extension with parameter types improvements.
- **[FEATURE]** (`ktx-actors`) `alpha` extension properties of `Actor` and `Stage` are now inlined.
- **[FEATURE]** (`ktx-app`) Added `KtxApplicationAdapter` interface which makes implementing all of `ApplicationListener` methods optional.
- **[FEATURE]** (`ktx-app`) Added `KtxInputAdapter` interface which makes implementing all of `InputProcessor` methods optional.
- **[FEATURE]** (`ktx-app`) Added `use` inlined methods to `Batch` and `ShaderProgram`, allowing to omit `begin()` and `end()` calls.
- **[FEATURE]** (`ktx-app`) Added `color` factory method to allow constructing LibGDX `Color` instances with named parameters.
- **[FEATURE]** (`ktx-app`) Added `Color.copy` extension method that allows to copy `Color` instances with optional
overriding of chosen values.
- **[CHANGE]** (`ktx-app`) `KotlinApplication#timeSinceLastRender` now has a protected default getter.
- **[CHANGE]** (`ktx-assets`) Static `AssetManager` instance container was deprecated. Static access to `AssetManager`
will be removed in the next release.
- **[FEATURE]** (`ktx-assets`) Added `load`, `loadAsset`, `loadOnDemand`, `getAsset`, `unload` and `unloadSafety`
extension methods to `AssetManager` to provide an alternative to equivalent utility functions using static manager instance.
- **[FEATURE]** (`ktx-assets`) Added `getLoader` and `setLoader` extension methods to `AssetManager` for `AssetLoader` handling.
- **[FEATURE]** (`ktx-async`) Implemented a new KTX module with multi-threaded operations utilities: `ktx-async`.
  - Implemented coroutines context using LibGDX threading model: `KtxAsync`. It resumes suspending operations on the
    main rendering thread with `Gdx.app.postRunnable` utility. It has to be initiated on the main thread with
    `enableKtxCoroutines`.
  - Added utility `ktxAsync` function which launches non-blocking coroutine using `KtxAsync` context.
  - Added `skipFrame` method that suspends the coroutine and resumes it on the next frame using `Gdx.app.postRunnable`.
  - Added `delay` method that offers non-blocking coroutine suspensions for the given period of time.
  - Added `httpRequest` method that performs asynchronous suspending HTTP request using LibGDX `Net` API.
  - Added `asynchronous` method, which allows to perform suspending operations on a separate thread.
  - `schedule` and `interval` utility methods added to ease the use of `com.badlogic.gdx.utils.Timer` API.
  - Added `HttpRequestResult`: a thread-safe `HttpResponse` wrapper that addresses [libgdx#4700](https://github.com/libgdx/libgdx/issues/4700).
- **[FEATURE]** (`ktx-collections`) Added `sortDescending`, `sortBy` and `sortByDescending` utility methods to LibGDX `Array`.
- **[FEATURE]** (`ktx-collections`) Added type aliases to LibGDX collections to avoid name collisions with standard library:
  - **`GdxArray`**: `com.badlogic.gdx.utils.Array`
  - **`GdxIntArray`**: `com.badlogic.gdx.utils.IntArray`
  - **`GdxFloatArray`**: `com.badlogic.gdx.utils.FloatArray`
  - **`GdxBooleanArray`**: `com.badlogic.gdx.utils.BooleanArray`
  - **`GdxCharArray`**: `com.badlogic.gdx.utils.CharArray`
  - **`GdxLongArray`**: `com.badlogic.gdx.utils.LongArray`
  - **`GdxShortArray`**: `com.badlogic.gdx.utils.ShortArray`
  - **`GdxSet`**: `com.badlogic.gdx.utils.ObjectSet`
  - **`GdxMap`**: `com.badlogic.gdx.utils.ObjectMap`
  - **`GdxList`**: `ktx.collections.PooledList`
- **[FEATURE]** (`ktx-collections`) `lastIndex` extension properties of LibGDX arrays are now inlined.
- **[FEATURE]** (`ktx-collections`) Added `component1()` and `component2()` operator extension methods to `Entry` classes
of LibGDX maps to support destructing syntax and simplify iteration.
- **[CHANGE]** (`ktx-i18n`) Static `I18NBundle` instance container was deprecated. Static access to `I18NBundle` will be
removed in the next release.
- **[CHANGE]** (`ktx-inject`) Static `Context` instance container was deprecated. Static access to `Context` will be
removed in the next release.
- **[FEATURE]** (`ktx-scene2d`, `ktx-vis`) `inCell` extension property added to `Table` children. Now you can easily access `Cell`
instance outside of the actors' building blocks.
- **[FEATURE]** (`ktx-scene2d`, `ktx-vis`) `inNode` extension property added to `Tree` children. Now you can easily access `Node`
instance outside of the actors' building blocks.
- **[FEATURE]** (`ktx-scene2d`, `ktx-vis`) fluent `cell` extension method added to `Table` children, allowing to configure `Cell`
properties outside of actors' building blocks.
- **[FEATURE]** (`ktx-scene2d`, `ktx-vis`) fluent `node` extension method added to `Tree` children, allowing to configure `Node`
properties outside of actors' building blocks.
- **[FEATURE]** (`ktx-scene2d`, `ktx-vis`) Resolved DSL scoping issues with Kotlin 1.1 `@DslMarker` API.
- **[CHANGE]** (`ktx-scene2d`, `ktx-vis`) Due to `@DslMarker` introduction, implicit access to parental widgets is no longer possible
in children building blocks. See `ktx-scene2d` or `ktx-vis` documentation for more info on the migration.
- **[CHANGE]** (`ktx-scene2d`) `KNode.invoke` extension method was moved directly to `KNode` API and no longer has to be imported.
- **[FEATURE]** (`ktx-style`) `Skin` instance is now available under lambda parameter of `skin` method init blocks.
- **[FEATURE]** (`ktx-style`, `ktx-style-vis`) Resolved DSL scoping issues with Kotlin 1.1 `@DslMarker` API.
- **[CHANGE]** (`ktx-style`, `ktx-style-vis`) Due to `@DslMarker` introduction, implicit access to `Skin` instance is no longer possible.
See `ktx-style` documentation for more info on the migration.
- **[FEATURE]** (`ktx-vis`) Added support for `HorizontalCollapsibleWidget`: `horizontalCollapsible` builder methods
added to all parental actors.
- **[FEATURE]** (`ktx-vis`) Added support for `VisTree` building using type-safe API.
- **[CHANGE]** (`ktx-vis`) Cells and nodes are now available as lambda parameters. See `ktx-vis` documentation for migration guide.

#### 1.9.6-b1

- **[UPDATE]** Updated to LibGDX 1.9.6.
- **[UPDATE]** Updated to Kotlin 1.1.0.
- **[FEATURE]** (`ktx-collections`) Added null-safe `size()` method to LibGDX `IntArray`, `FloatArray` and `BooleanArray`
collections.
- **[FEATURE]** (`ktx-collections`) Added null-safe extension property `lastIndex` to LibGDX `ArrayList` equivalents:
`Array`, `IntArray`, `FloatArray` and `BooleanArray`.

#### 1.9.5-b1

- **[UPDATE]** Updated to LibGDX 1.9.5.

#### 1.9.4-b2

- **[FEATURE]** (`ktx-actors`) Added `Actor.onKeyUp` and `Actor.onKeyDown` extension methods that attach
`EventListener` implementations listening to `InputEvent` instances.
- **[FEATURE]** (`ktx-app`) Implemented `ktx-app` module.
  - `KotlinApplication` is an `ApplicationAdapter` equivalent with fixed rendering time step.
  - `clearScreen` utility function allows to easily clear the application screen.
  - `LetterboxingViewport` is a `Viewport` implementation that combines `ScreenViewport` and `FitViewport` behaviors.
- **[FEATURE]** (`ktx-vis`) Added `ListViewStyle` support to `ListView` factory methods.
- **[FEATURE]** (`ktx-vis`) Added top level `tab()` method.
- **[FEATURE]** (`ktx-vis-style`) Added `ListViewStyle` factory method: `listView`.
- **[FIX]** (`ktx-scene2d`) Added missing `TextButton` factory methods.

#### 1.9.4-b1

- **[UPDATE]** Updated to LibGDX 1.9.4.
- **[FEATURE]** (`ktx-actors`) Implemented `ktx-actors` module.
  - `isShown`, `centerPosition`, `setKeyBoardFocus` and `setScrollFocus` extension methods for `Actor`.
  - `contains` operator extension method of `Group` and `Stage` supporting `actor in group` syntax.
  - `+` and `-` operator for adding actors to `Group` and `Stage`.
  - `alpha` extension field for `Actor` and `Stage`.
  - Lambda consuming `onChange`, `onClick`, `onKey`, `onScrollFocus` and `onKeyboardFocus` extension methods for `Actor`, allowing to quickly define event listeners.
  - `+` and `-` operator extension methods can be used to add `Action` instances to a `Stage`.
  - `Action.then` infix extension method can be used to chain actions into sequences.
  - `Action.repeatForever` wraps an action in a `RepeatAction` without a repetitions limit.
- **[FEATURE]** (`ktx-assets`) Implemented `ktx-assets` module.
  - `Assets.manager` global `AssetManager` instance.
    - `load` function can be used to load assets asynchronously via the global `AssetManager` instance. `loadOnDemand` can be used to load assets immediately in a blocking manner. `unload` can unload the assets.
    - `asset` function can be used to access loaded assets from the global `AssetManager` instance.
    - `isLoaded` allows to check if an asset has been loaded by the global `AssetManager`.
  - `disposeSafely` and lambda consuming `dispose` were added to `Disposable`.
  - `Iterable` and `Array` instances storing `Disposable` elements can now be disposed.
  - `Exception.ignore` extension method was added for explicit no-op handling of exceptions.
  - `Pool.invoke` operator extension method was added as an alternative to `Pool.obtain`.
  - `Pool.invoke(T)` operator extension method was added as an alternative to `Pool.free(T)`.
  - Lambda consuming `pool` factory function was added.
  - `toClasspathFile`, `toInternalFile`, `toLocalFile`, `toExternalFile` and `toAbsoluteFile` converter methods added to `FileHandle`.
  - `file` factory function was added.
- **[FEATURE]** (`ktx-collections`) Implemented `ktx-collections` module.
  - `Array` factory function `gdxArrayOf` and converter method `toGdxArray`.
  - `Array` extensions including: `isEmpty`, `isNotEmpty`, `size`, `+`, `-`, `getLast`, `removeLast`, `get`, `addAll`, `removeAll`, `iterate`.
  - `ObjectSet` factory function `gdxSetOf` and converter method `toGdxSet`.
  - `ObjectSet` extensions including: `isEmpty`, `isNotEmpty`, `size`, `+`, `-`, `addAll`, `removeAll`, `iterate`.
  - `ObjectMap` factory function `gdxMapOf` and `IdentityMap` factory `gdxIdentityMapOf`.
  - Maps extensions including: `isEmpty`, `isNotEmpty`, `size`, `contains` (`in`), `set` (`[]`), `iterate`, `toGdxSet`.
  - Lambda consuming `Iterable.toGdxMap` allows to convert any collection to a `ObjectMap`.
  - `PooledList` collection as an alternative to `PooledLinkedList`. Includes `gdxListOf` and `toGdxList` factory methods.
- **[FEATURE]** (`ktx-i18n`) Implemented `ktx-i18n` module.
  - `I18n.defaultBundle` global `I18NBundle` instance loaded by `I18n.load`.
  - `addListener`, `removeListener` and `clearListeners` of `I18n` allow to handle the lifecycle of the global `I18NBundle`.
  - `nls` functions allow to access global `I18NBundle`.
  - `I18NBundle.get` operator function improves access to the bundle lines.
  - `BundleLine` is an interface designed to be implemented by enums that match bundle line names stored in an i18n properties file.
- **[FEATURE]** (`ktx-inject`) Implemented `ktx-inject` module.
  - `Context` is the core of the dependency injection framework, storing the registered singletons and providers.
  - Global `Context` instance is available via `ContextContainer.defaultContext`.
  - `inject` and `provider` functions allow to extract instances and providers of selected type from the global `Context`.
  - `register` allows to add singletons and providers to the global `Context`.
- **[FEATURE]** (`ktx-log`) Implemented `ktx-log` module.
  - `debug`, `info` and `error` functions allow to log data with the LibGDX logging API.
  - `logger` factory function provides instances of the KTX `Logger` that wraps LibGDX logging API.
- **[FEATURE]** (`ktx-math`) Implemented `ktx-math` module.
  - `vec2`, `vec3`, `mat3` and `mat4` factory methods for `Vector2`, `Vector3`, `Matrix3` and `Matrix4` respectively.
  - `+`, `-`, `*`, `/`, `-`, `++`, `--`, `<`, `>`, `<=`, `>=` operators support for `Vector2` and `Vector3`.
  - `+`, `-`, `*`, `!`, `-` operators support for `Matrix3` and `Matrix4`.
  - `Vector2`, `Vector3`, `Matrix3` and `Matrix4` are now decomposable into 2, 3, 9 and 16 components respectively.
- **[FEATURE]** (`ktx-scene2d`) Implemented `ktx-scene2d` module.
  - Added DSL for constructing complex `Scene2D` widgets.
    - Factory methods for parental actors: `buttonTable`, `container`, `dialog`, `horizontalGroup`, `scrollPane`, `splitPane`, `stack`, `table`, `tree`, `verticalGroup` and `window`.
    - Factory methods for secondary parental actors: `button`, `checkBox`, `imageButton` and `imageTextButton`.
    - Factory methods for child actors: `image`, `label`, `list`, `progressBar`, `selectBox`, `slider`, `textArea`, `textField` and `touchpad`.
- **[FEATURE]** (`ktx-style`) Implemented `ktx-style` module.
  - `skin` factory methods producing `Skin` instances.
  - `get` operator infix function for quick access of `Skin` resources.
  - `set` operator function for quick modification of `Skin` resources.
  - Factory methods for styles of `Scene2D` widgets: `color`, `button`, `checkBox`, `imageButton`, `imageTextButton`, `label`, `list`, `progressBar`, `selectBox`, `slider`, `splitPane`, `textButton`, `textField`, `textTooltip`, `touchpad`, `tree`, `window`.
- **[FEATURE]** (`ktx-vis`) Implemented `ktx-vis` module.
  - Added DSL for constructing complex `VisUI` widgets.
- **[FEATURE]** (`ktx-vis-style`) Implemented `ktx-vis-style` module.
  - Factory methods for styles of `VisUI` widgets.
