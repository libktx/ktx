#### 1.9.8-SNAPSHOT

- **[UPDATE]** Updated to Kotlin 1.2.51.
- **[UPDATE]** Updated to Kotlin Coroutines 0.24.0.

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
