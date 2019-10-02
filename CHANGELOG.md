#### 1.9.10-SNAPSHOT

- **[FEATURE]** (`ktx-style`) Added more extension methods to Skin, with reified type and name parameter that
defaults to the "default" style name. `optional`, `add`, `remove`, `has` and `getAll` extension methods were added.
The overloaded `+=` operator can also be used to add resource with "default" name.

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
  - `triange`
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

- **[FEATURE]** (`ktx-collections`) Added `map`, `filter` and `flatten` extension methods that return LibGDX collections.
- **[FEATURE]** (`ktx-collections`) `PooledList` now properly implements `hashCode` and `equals`.
- **[FEATURE]** (`ktx-app`) Added `KtxGame`: **KTX** equivalent of LibGDX `Game`.
- **[FEATURE]** (`ktx-app`) Added `KtxScreen`: adapter of the LibGDX `Screen` interface making all methods optional to implement.
- **[FEATURE]** (`ktx-app`) Added `emptyScreen` utility method returning a no-op implementation of `Screen`.
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
- **[FEATURE]** (`ktx-vis`) Added `ListViewStyle` support to `ListView` factory methods.
- **[FEATURE]** (`ktx-vis`) Added top level `tab()` method.
- **[FEATURE]** (`ktx-vis-style`) Added `ListViewStyle` factory method: `listView`.
- **[FIX]** (`ktx-scene2d`) Added missing `TextButton` factory methods.

#### 1.9.4-b1

- **[UPDATE]** Updated to LibGDX 1.9.4.
- **[FEATURE]** (`ktx-actors`) Implemented `ktx-actors` module.
- **[FEATURE]** (`ktx-assets`) Implemented `ktx-assets` module.
- **[FEATURE]** (`ktx-collections`) Implemented `ktx-collections` module.
- **[FEATURE]** (`ktx-i18n`) Implemented `ktx-i18n` module.
- **[FEATURE]** (`ktx-inject`) Implemented `ktx-inject` module.
- **[FEATURE]** (`ktx-log`) Implemented `ktx-log` module.
- **[FEATURE]** (`ktx-math`) Implemented `ktx-math` module.
- **[FEATURE]** (`ktx-scene2d`) Implemented `ktx-scene2d` module.
- **[FEATURE]** (`ktx-style`) Implemented `ktx-style` module.
- **[FEATURE]** (`ktx-vis`) Implemented `ktx-vis` module.
- **[FEATURE]** (`ktx-vis-style`) Implemented `ktx-vis-style` module.
