# Composable Scene2D DSL Prototype Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add separated actor placement/init and reusable `Scene2dFactoryDescriptor` recipes to the `scene2d` module without compiler tooling.

**Architecture:** Keep `KWidget<out Storage>` as the parent capability. Add `mount` as the shared store → placement → init primitive, and let `Scene2dFactoryDescriptor<A>` hold a `RootWidget` recipe that is rebuilt on every invocation. Convert only `table` to the separated callback shape in this prototype; leave other built-in factories unchanged.

**Tech Stack:** Kotlin 2.1.10, libGDX 1.13.1, Gradle Kotlin DSL, JUnit 4, Mockito-Kotlin, Ktlint.

**Spec:** `docs/superpowers/specs/2026-08-29-scene2d-composable-dsl-prototype-design.md`

## Global Constraints

- Preserve `Scene2DSkin.defaultSkin` behavior.
- Avoid reflection, KSP, compiler plugins, generated adapters, and registration.
- Create fresh actors for every `Scene2dFactoryDescriptor` invocation.
- Use `storeActor(actor)`, then placement, then actor init.
- Keep non-table built-in factory signatures unchanged.
- Do not add two-typed factory scopes or parent-aware construction.
- Do not add a metadata annotation; avoid classifier-name collision and keep descriptor API minimal.
- Do not introduce source-compatible legacy/new `table` overloads; trailing-lambda ambiguity is deferred.
- Follow existing formatting and test conventions in `scene2d`.

---

### Task 1: Add failing factory composition tests

**Files:**
- Create: `scene2d/src/test/kotlin/ktx/scene2d/Scene2dFactoryTest.kt`

**Interfaces:**
- Consumes: planned `scene2dFactory`, `Scene2dFactoryDescriptor.invoke`, separated `table`, and `mount` APIs.
- Produces: executable acceptance coverage for core implementation tasks.

- [ ] **Step 1: Write failing tests for factory values and separated callbacks**

Create `Scene2dFactoryTest : ApplicationTest` with these tests:

```kotlin
private val sidePane = scene2dFactory {
  table {
    label("Inventory")
  }
}

@Test
fun `factory value can be invoked in root scope and creates fresh actors`() {
  val first = scene2d { sidePane() }
  val second = scene2d { sidePane() }

  assertNotSame(first, second)
  assertEquals("Inventory", (first.children.first() as Label).text.toString())
  assertEquals("Inventory", (second.children.first() as Label).text.toString())
}

@Test
fun `factory trailing init configures actor and placement configures table cell`() {
  val root = scene2d.table {
    sidePane(
      placement = {
        growY()
      },
    ) {
      label("Extra content")
    }
  }

  val pane = root.children.first() as KTableWidget
  assertEquals(2, pane.children.size)
  assertEquals("Extra content", (pane.children[1] as Label).text.toString())
  assertTrue(root.getCell(pane).expandY)
}

@Test
fun `same factory mounts in table and group scopes`() {
  val table = scene2d.table { sidePane() }
  val group = scene2d.stack { sidePane() }

  assertSame(table, (table.children.first() as KTableWidget).parent)
  assertSame(group, group.children.first().parent)
}
```

Add a custom actor proving `mount` delegates construction, placement, and init:

```kotlin
private class HealthBar(val value: Float) : Actor()

private inline fun <S> KWidget<S>.healthBar(
  value: Float,
  placement: S.() -> Unit = {},
  init: (@Scene2dDsl HealthBar).() -> Unit = {},
): HealthBar = mount(HealthBar(value), placement, init)

@Test
fun `custom actor factory uses mount`() {
  val root = scene2d.stack {
    healthBar(0.75f) {
      name = "health"
    }
  }

  assertEquals("health", root.children.first().name)
  assertEquals(0.75f, (root.children.first() as HealthBar).value, TOLERANCE)
}
```

Use imports already established by `factoryTest.kt`: `Actor`, `Label`, `assertEquals`, `assertNotSame`, `assertSame`, `assertTrue`, and `org.junit.Test`.

- [ ] **Step 2: Run tests and verify expected compile failure**

Run:

```bash
./gradlew :scene2d:test --tests ktx.scene2d.Scene2dFactoryTest
```

Expected: compilation fails because `scene2dFactory`, `Scene2dFactoryDescriptor`, `mount`, and separated `table` do not exist yet.

- [ ] **Step 3: Commit failing tests**

```bash
git add scene2d/src/test/kotlin/ktx/scene2d/Scene2dFactoryTest.kt
git commit -m "test: specify composable Scene2D factory behavior"
```

---

### Task 2: Implement factory kernel

**Files:**
- Modify: `scene2d/src/main/kotlin/ktx/scene2d/widget.kt`
- Modify: `scene2d/src/main/kotlin/ktx/scene2d/factory.kt`

**Interfaces:**
- Consumes: failing tests from Task 1 and existing `KWidget`, `RootWidget`, and `scene2d` APIs.
- Produces: `Scene2dFactoryDescriptor<A>`, `scene2dFactory`, `mount`, and factory-value invocation.

- [ ] **Step 1: Add recipe descriptor and constructor function**

In `factory.kt`, add imports for `kotlin.contracts.ExperimentalContracts`, `InvocationKind`, and contract helpers if needed by the implementation. Define:

```kotlin
@Scene2dDsl
class Scene2dFactoryDescriptor<A : Actor> internal constructor(
  internal val build: RootWidget.() -> A,
)

fun <A : Actor> scene2dFactory(
  build: RootWidget.() -> A,
): Scene2dFactoryDescriptor<A> = Scene2dFactoryDescriptor(build)
```

The descriptor must contain only the recipe. It must not construct or cache an actor during descriptor creation.

- [ ] **Step 2: Add `mount` with exact operation order**

In `factory.kt`, add:

```kotlin
@Scene2dDsl
inline fun <S, A : Actor> KWidget<S>.mount(
  actor: A,
  placement: S.() -> Unit = {},
  init: (@Scene2dDsl A).() -> Unit = {},
): A {
  val stored = storeActor(actor)
  stored.placement()
  actor.init()
  return actor
}
```

Add an exact-once contract for both lambdas only if Kotlin accepts it without changing existing module conventions. Do not add rollback or actor reuse handling.

- [ ] **Step 3: Add member-extension factory invocation**

Inside `KWidget<out Storage>` in `widget.kt`, add:

```kotlin
@Scene2dDsl
operator fun <A : Actor> Scene2dFactoryDescriptor<A>.invoke(
  placement: @UnsafeVariance Storage.() -> Unit = {},
  init: (@Scene2dDsl A).() -> Unit = {},
): A {
  return mount(scene2d(build), placement, init)
}
```

The factory recipe must execute through `scene2d(build)`, so nested built-ins use `RootWidget` and each call creates a new actor tree. Keep `@UnsafeVariance` limited to the placement receiver.

- [ ] **Step 4: Run focused tests and resolve only kernel compile errors**

Run:

```bash
./gradlew :scene2d:test --tests ktx.scene2d.Scene2dFactoryTest
```

Expected: tests compile and pass except for failures caused by the not-yet-migrated `table` signature. Fix type/signature errors in the new kernel only; do not convert other factories.

- [ ] **Step 5: Commit kernel implementation**

```bash
git add scene2d/src/main/kotlin/ktx/scene2d/widget.kt scene2d/src/main/kotlin/ktx/scene2d/factory.kt
git commit -m "feat: add composable Scene2D factory kernel"
```

---

### Task 3: Convert table to separated placement and init

**Files:**
- Modify: `scene2d/src/main/kotlin/ktx/scene2d/factory.kt`
- Modify: `scene2d/src/test/kotlin/ktx/scene2d/factoryTest.kt`
- Modify: `scene2d/src/test/kotlin/ktx/scene2d/StageWidgetTest.kt` if table callback parameters are used there

**Interfaces:**
- Consumes: `mount` from Task 2.
- Produces: `KWidget<S>.table(skin, placement, init)` returning `KTableWidget`.

- [ ] **Step 1: Change `table` to separated callbacks**

Replace the existing table factory with:

```kotlin
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.table(
  skin: Skin = Scene2DSkin.defaultSkin,
  placement: S.() -> Unit = {},
  init: KTableWidget.() -> Unit = {},
): KTableWidget {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  return mount(
    actor = KTableWidget(skin),
    placement = placement,
    init = init,
  )
}
```

Keep `skin` first so existing `table()` and `table(skin = customSkin)` calls remain natural. Do not add a second legacy overload.

- [ ] **Step 2: Update tests that explicitly consume table storage**

Search:

```bash
rg -n "table\s*\{|table\(" scene2d/src/test/kotlin/ktx/scene2d
```

Change table trailing lambdas from `KTableWidget.(S) -> Unit` form to actor receiver form. Move cell assertions into `placement = { ... }` or use `inCell` from the created actor. Preserve all existing behavior assertions.

- [ ] **Step 3: Run focused and existing factory tests**

Run:

```bash
./gradlew :scene2d:test --tests ktx.scene2d.Scene2dFactoryTest --tests ktx.scene2d.RootActorFactoriesTest --tests ktx.scene2d.NoInitBlockActorFactoriesTest --tests ktx.scene2d.InlinedInitBlockActorFactoriesTest
```

Expected: PASS. Existing non-table callback tests must remain unchanged and green.

- [ ] **Step 4: Commit table migration**

```bash
git add scene2d/src/main/kotlin/ktx/scene2d/factory.kt scene2d/src/test/kotlin/ktx/scene2d/factoryTest.kt scene2d/src/test/kotlin/ktx/scene2d/StageWidgetTest.kt
git commit -m "feat: separate Scene2D table placement from init"
```

---

### Task 4: Document supported composition patterns

**Files:**
- Modify: `scene2d/README.md`

**Interfaces:**
- Consumes: public APIs delivered by Tasks 2–3.
- Produces: user-facing documentation for the prototype boundary.

- [ ] **Step 1: Add factory-value section after existing builder customization guidance**

Document that `scene2dFactory` stores a recipe and creates fresh actors per invocation. Include this exact pattern:

```kotlin
private val sidePane = scene2dFactory {
  table {
    label("Inventory")
  }
}

val root = scene2d.table {
  sidePane(placement = { growY() }) {
    label("Extra content")
  }
}
```

Explain that the trailing lambda configures the created actor while `placement` configures the parent storage object.

- [ ] **Step 2: Add custom and parameterized factory guidance**

Include this custom actor extension:

```kotlin
inline fun <S> KWidget<S>.healthBar(
  value: Float,
  placement: S.() -> Unit = {},
  init: (@Scene2dDsl HealthBar).() -> Unit = {},
): HealthBar = mount(HealthBar(value), placement, init)
```

Explain that parameterized or parent-aware components remain ordinary `KWidget` extension functions. State that factories do not cache actors.

- [ ] **Step 3: Add prototype limitations**

State that only `table` uses separated callbacks in this first prototype, other built-ins retain existing callback forms, and compiler plugins/KSP/reflection are not required. Do not document nonexistent compatibility overloads.

- [ ] **Step 4: Run documentation-aware formatting/checks**

Run:

```bash
./gradlew :scene2d:check
```

Expected: PASS, including Kotlin formatting and documentation checks configured by the repository.

- [ ] **Step 5: Commit documentation**

```bash
git add scene2d/README.md
git commit -m "docs: explain composable Scene2D factories"
```

---

### Task 5: Full verification and review handoff

**Files:**
- Review all files changed by Tasks 1–4.

**Interfaces:**
- Consumes: complete prototype implementation and test/doc commits.
- Produces: verified branch with residual-risk report.

- [ ] **Step 1: Run full module tests**

Run:

```bash
./gradlew :scene2d:test
```

Expected: PASS for all Scene2D tests.

- [ ] **Step 2: Run full module checks**

Run:

```bash
./gradlew :scene2d:check
```

Expected: PASS with no formatting or API validation failures.

- [ ] **Step 3: Inspect diff and status**

Run:

```bash
git diff master...HEAD --check
git diff master...HEAD --stat
git status --short --branch
```

Expected: only approved spec, plan, Scene2D implementation/tests/docs are tracked; generated `.pi/` remains untracked and is not staged.

- [ ] **Step 4: Commit any narrowly scoped verification fixes**

If checks expose a real implementation defect, fix it with a regression test first, rerun the affected command, then commit the approved prototype files:

```bash
git add scene2d/src/main/kotlin/ktx/scene2d/widget.kt scene2d/src/main/kotlin/ktx/scene2d/factory.kt scene2d/src/test/kotlin/ktx/scene2d/Scene2dFactoryTest.kt scene2d/README.md
git commit -m "fix: address Scene2D prototype verification finding"
```

Do not broaden scope into full factory migration.
