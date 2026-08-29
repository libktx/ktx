# Composable Scene2D DSL Prototype

## Status

Approved prototype design. This document covers the first implementation experiment from
`ktx_scene2d_composable_dsl_design.docx`; it does not implement the full factory migration.

## Goal

Make reusable actor recipes compose through the same public kernel as built-in Scene2D DSL
constructs, while separating actor initialization from parent-specific placement.

The prototype must preserve `Scene2DSkin.defaultSkin`, avoid reflection/KSP/compiler plugins,
and leave non-table built-in factory APIs unchanged.

## Scope

### In scope

- Add `mount(actor, placement, init)` as the separated builder primitive.
- Add `Scene2dFactoryDescriptor<A : Actor>` and `scene2dFactory` for reusable actor recipes.
- Add a member-extension `invoke` operator so factory values are callable inside any `KWidget`
  scope.
- Convert `table` to separated `placement` and actor `init` callbacks.
- Add focused runtime and compile-time coverage for root, table, group, nested, and custom
  factories.
- Document factory values, placement, parameterized extension factories, and semantic fragments.

### Out of scope

- Converting every built-in factory to `mount`.
- Source-compatible legacy/new overloads for `table`; those overloads risk ambiguous calls with
  trailing lambdas and are deferred to the broader migration.
- Factory metadata annotation; the prototype keeps descriptor API minimal and avoids optional
  tooling.
- Removing `Scene2DSkin.defaultSkin`.
- Two-typed factories with a separate actor scope.
- Reflection, KSP, compiler plugins, registration, or generated adapters.
- Parent-aware construction for factories that need parent information.

## API design

### Mounting

```kotlin
inline fun <S, A : Actor> KWidget<S>.mount(
  actor: A,
  placement: S.() -> Unit = {},
  init: A.() -> Unit = {},
): A
```

`mount` stores the actor, applies placement to the returned storage, applies actor
initialization, and returns the actor. Storage remains parent-specific: `Cell` for tables,
`KNode` for trees, and `Actor` for groups/root.

The operation order is:

1. `storeActor(actor)`
2. `stored.placement()`
3. `actor.init()`
4. return `actor`

Exceptions propagate without rollback, matching current builder behavior and libGDX ownership
semantics.

### Factory descriptors

```kotlin
@Scene2dDsl
class Scene2dFactoryDescriptor<A : Actor> internal constructor(
  internal val build: RootWidget.() -> A,
)

fun <A : Actor> scene2dFactory(
  build: RootWidget.() -> A,
): Scene2dFactoryDescriptor<A>
```

A factory stores a recipe, not an actor. Each invocation executes the recipe through `scene2d`
and therefore creates a fresh actor tree.

`KWidget` exposes:

```kotlin
operator fun <A : Actor> Scene2dFactoryDescriptor<A>.invoke(
  placement: @UnsafeVariance Storage.() -> Unit = {},
  init: A.() -> Unit = {},
): A
```

The operator builds the detached root, then uses `mount` to store and configure it. The
`@UnsafeVariance` annotation is limited to the placement receiver required by covariant
`KWidget<out Storage>`.

### Table factory

`table` receives separated callbacks:

```kotlin
inline fun <S> KWidget<S>.table(
  skin: Skin = Scene2DSkin.defaultSkin,
  placement: S.() -> Unit = {},
  init: KTableWidget.() -> Unit = {},
): KTableWidget
```

Placement configures the parent-created storage object. The trailing lambda configures the
created `KTableWidget` and can create nested children. Other built-in factories continue using
current callback shapes during this prototype.

## Data flow examples

Private reusable component:

```kotlin
private val sidePane = scene2dFactory {
  table {
    label("Inventory")
  }
}

scene2d.table {
  sidePane(placement = { growY() }) {
    label("Extra content")
  }
}
```

Custom actor factory:

```kotlin
inline fun <S> KWidget<S>.healthBar(
  value: Float,
  placement: S.() -> Unit = {},
  init: HealthBar.() -> Unit = {},
): HealthBar = mount(HealthBar(value), placement, init)
```

Parameterized components remain ordinary private or public `KWidget` extension functions.
Semantic fragments remain ordinary scoped functions and do not require an `Actor` subclass.

## Files

- `scene2d/src/main/kotlin/ktx/scene2d/widget.kt`: `KWidget` factory invocation operator.
- `scene2d/src/main/kotlin/ktx/scene2d/factory.kt`: `mount`, factory descriptor creation, and
  separated `table` implementation.
- `scene2d/src/test/kotlin/ktx/scene2d/Scene2dFactoryTest.kt`: focused factory and placement
  behavior tests.
- `scene2d/README.md`: usage and extension guidance.

## Verification

Run:

```text
./gradlew :scene2d:test
./gradlew :scene2d:check
```

Tests must verify:

- Factory values invoke in root, table, and group scopes.
- Factory invocation creates fresh actors on repeated calls.
- Trailing init receives the created actor and can append children.
- Placement configures parent storage without exposing storage in actor init.
- One factory mounts under table and group without changing its definition.
- Nested factories compose.
- Custom actor factories delegate to `mount`.
- Default skin behavior remains unchanged.
- Existing non-table factory coverage remains green.

Formatting follows existing repository Gradle/Ktlint checks.

## Migration boundary

This prototype answers whether factory values plus a member-extension `invoke` provide natural
DSL composition without hidden tooling. If successful, later work can convert other built-ins
to `mount`, add staged compatibility bridges, and expand documentation. Those changes require a
separate migration design because overload ambiguity and binary/source compatibility need their
own treatment.
