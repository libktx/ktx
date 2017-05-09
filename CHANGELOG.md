#### 1.9.6-SNAPSHOT

- **[UPDATE]** Updated to Kotlin 1.1.2.
- **[UPDATE]** Updated to Kotlin Coroutines to 0.15.
- **[CHANGE]** (`ktx-assets`) Static `AssetManager` instance container - `Assets` - was removed. All top level functions
depending on the global `AssetManager` were removed.
- **[FEATURE]** (`ktx-assets`) Added `FileType.getResolver` extension method creating `FileHandleResolver` instances.
- **[FEATURE]** (`ktx-assets`) Added `FileHandleResolver.withPrefix` extension method decorating resolvers with `PrefixFileHandleResolver`.
- **[FEATURE]** (`ktx-assets`) Added `FileHandleResolver.forResolutions` extension method decorating resolvers with `ResolutionFileResolver`.
- **[FEATURE]** (`ktx-assets`) Added `resolution` function constructing `ResolutionFileResolver.Resolution` instances.
- **[FEATURE]** (`ktx-box2d`) Implemented a new **KTX** module with Box2D physics engine utilities: `ktx-box2d`.
  - `world` factory method constructing `World` instances.
  - `World.body` extension method providing type-safe builder DSL for `Body` instances.
  - `FixtureDef` builder methods supporting all shapes (`CircleShape`, `PolygonShape`, `ChainShape`, `EdgeShape`).
  - `FixtureDef.filder` extension methods simplifying `Filter` properties setup.
  - `BodyDefinition` is a `BodyDef` extension providing `Body` building DSL. Used internally by `World.body`.
  - `earthGravity` property allowing to set `World` gravity roughly matching Earth's gravity.
- **[FEATURE]** (`ktx-async`) Added `AssetStorage`: a lightweight coroutines-based alternative to `AssetManager`.
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
