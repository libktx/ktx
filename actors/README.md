[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-actors.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-actors)

# KTX: General `Scene2D` utilities

Extensions and utilities for stages, actors, actions and event listeners.

### Why?

`Scene2D` API is not that easy to extend in the first place - due to hidden fields and methods, sometimes copy-pasting
a widget class and modifying the relevant lines is the only way to implement an actor "extension". Most event listeners
cannot be used with Kotlin's pleasant lambda syntax. Actions API is pretty powerful, but can be nearly unreadable when
it comes to chaining.

### Guide

#### Actors

- Null-safe `Actor.isShown()` method was added to make it possible to check if actor is currently on a `Stage`.
- `Actor.centerPosition` extension method was added to allow quick actor centering, hiding the necessary math.
- `Group.contains(Actor)` method was added to support `in` operator. You can check if an `Actor` is a direct child of
a `Group` with `actor in group` syntax.
- `Group` and `Stage` support actor adding and removal through `+=` and `-=` operators.
- `Stage.contains(Actor)` method was added to support `in` operator. This will report `true` if the `Actor` is on the
`Stage` (it does not have to be a direct child of `Stage` root group).
- `Actor.alpha` and `Stage.alpha` inlined extension properties were added to support easy modification of `Color.a` value.
- `Actor.setKeyboardFocus` and `.setScrollFocus` allow to quickly (un)focus the actor on its stage.

#### Event listeners

- Lambda-compatible `Actor.onChange` method was added. Allows listening to `ChangeEvents`.
- Lambda-compatible `Actor.onClick` method was added. Attaches `ClickListeners`.
- Lambda-compatible `Actor.onTouchDown` and `Actor.onTouchUp` methods were added. Attaches `ClickListeners`. 
- Lambda-compatible `Actor.onKey` method was added. Allows listening to `InputEvents` with `keyTyped` type.
- Lambda-compatible `Actor.onKeyDown` and `Actor.onKeyUp` methods were added. They allow listening to `InputEvents`
with `keyDown` and `keyUp` type, consuming key code of the pressed or released key (see libGDX `Keys` class).
- Lambda-compatible `Actor.onScrollFocus` method was added. Allows listening to `FocusEvents` with `scroll` type.
- Lambda-compatible `Actor.onKeyboardFocus` method was added. Allows listening to `FocusEvents` with `keyboard` type.
- `KtxInputListener` is an open class that extends `InputListener` with no-op default implementations and type
improvements (nullability data).
- `onChangeEvent`, `onClickEvent`, `onTouchEvent`, `onKeyEvent`, `onKeyDownEvent`, `onKeyUpEvent`, `onScrollFocusEvent`
and `onKeyboardFocusEvent` `Actor` extension methods were added. They consume the relevant `Event` instances as lambda
parameters. Both listener factory variants are inlined, but the ones ending with *Event* provide more lambda parameters
and allow to inspect the original `Event` instance that triggered the listener. Regular listener factory methods should
be enough for most use cases.

#### Actions

- Global actions can be added and removed from `Stage` with `+=` and `-=` operators.
- Actions can be added and removed to individual `Actor` instances with `+=` and `-=` operators.
- `Action.then` *infix* extension function allows easy creation of action sequences with pleasant syntax.
Either wraps the two actions in a `SequenceAction`, or if the left action is already a `SequenceAction`,
adds the right action to it so long chains result in a single `SequenceAction`.
- `Action.along` *infix* extension function allows easy creation of parallel actions with pleasant syntax.
Either wraps the two actions in a `ParallelAction`, or if the left action is already a `ParallelAction`,
adds the right action to it so long chains result in a single `ParallelAction`.
- `+` operator can be used to create action sequences (alternative to `then`).
The operator is non-mutating, so it wraps the two actions every time.
For long chains, the `then` function may be preferred to avoid creating multiple nested `SequenceActions`.
- `/` operator can be used combine actions in parallel (alternative to `along`).
The operator is non-mutating, so it wraps the two actions every time.
For long chains, the `along` function may be preferred to avoid creating multiple nested ParallelActions.
- `+=` operator adds an action to an existing `SequenceAction` or `ParallelAction`.
- `Action.repeat` and `Action.repeatForever` allow to repeat a chosen action by wrapping it with a `RepeatAction`.

#### Widgets

- `txt` inlined extension properties added to `Label` and `TextButton` widgets. Since types of `getText` and `setText`
methods in both of these widgets are not compatible (`get` returns `StringBuilder`, `set` consumes a `CharSequence`),
an extension was necessary to let these widgets fully benefit from idiomatic Kotlin properties syntax. Since Kotlin
properties cannot overshadow Java methods, property was renamed to `txt`.

### Usage examples

Centering actor on a stage:
```kotlin
import ktx.actors.*

window.centerPosition()
```

Adding and removing actors with operators:
```kotlin
import ktx.actors.*

table += button
table -= label
stage += table
```

Checking if actor is on a stage or in a group:
```kotlin
import ktx.actors.*

button in stage
button in table
```

Quickly accessing actor and stage alpha color value:
```kotlin
import ktx.actors.*

label.alpha = 0.5f
stage.alpha = 0.2f
```

Focusing events on actors:
```kotlin
import ktx.actors.*

textField.setKeyboardFocus(true)
scrollPane.setScrollFocus(false)
```

Adding a `ChangeListener` to an `Actor`:

```kotlin
import com.badlogic.gdx.scenes.scene2d.ui.Button
import ktx.actors.*

fun attachListener(button: Button) {
  button.onChange {
    println("Button changed!")
  }

  button.onChangeEvent { changeEvent ->
    // If you need access to the original ChangeEvent, use this expanded method variant.
    println("$this actor changed by $changeEvent!")
  }  
}

```

Adding a `ClickListener` to an `Actor`:

```kotlin
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.actors.*

fun attachClickListener(label: Label) {
  label.onClick {
    println("Label clicked!")
  }

  label.onClickEvent { inputEvent ->
    // If you need access to the original InputEvent, use this expanded method variant.
    println("$this actor clicked with $inputEvent!")
  }
  label.onClickEvent { inputEvent, x, y ->
    // If you need access to the local actor click coordinates, use this expanded method variant.
    println("$this actor clicked with $inputEvent at ($x, $y)!")
  }
}
```

Adding an event listener for touch events to an `Actor`:

```kotlin
import com.badlogic.gdx.scenes.scene2d.ui.Button
import ktx.actors.*

fun attachTouchListeners(button: Button) {
  button.onTouchDown {
    println("Button down!")
  }

  button.onTouchUp {
    println("Button up!")
  }

  button.onTouchEvent(
    // If you want to define a single shared listener or need access to the original InputEvent,
    // use this expanded method variant:
    onDown = { inputEvent -> println("$this actor touched with $inputEvent!") },
    onUp = { inputEvent -> println("$this actor released with $inputEvent!") }
  )
  // ...or with a single lambda.
  // In this case you can use InputEvent.Type to distinguish between touchDown and touchUp events:
  button.onTouchEvent { inputEvent  -> println("$this actor ${inputEvent.type} with $inputEvent!") }

  // There are also additional variants with local touch coordinates and mouse/cursor data.
  // See the documentation or sources for more details.
}
```

Adding an `EventListener` which consumes typed characters:

```kotlin
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import ktx.actors.*

fun attachKeyListener(textField: TextField) {
  textField.onKey { key ->
    println("Typed $key char to text field!")
  }

  textField.onKeyEvent { inputEvent, key ->
    // If you need access to the original InputEvent, use this expanded method variant.
    println("Typed $key char to $this actor with $inputEvent!")
  }
}
```

Adding `EventListeners` which listen to `FocusEvent` instances:

```kotlin
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import ktx.actors.*

fun attachScrollFocusListener(scrollPane: ScrollPane) {
  // FocusEvent with scroll type:
  scrollPane.onScrollFocus { focused ->
    println("Scroll pane is focused: $focused!")
  }

  scrollPane.onScrollFocusEvent { focusEvent ->
    // If you need access to the original FocusEvent, use this expanded method variant.
    println("$this actor is focused: ${focusEvent.isFocused}!")
  }
}

fun attachKeyboardFocusListener(textField: TextField) {
  // FocusEvent with keyboard type:
  textField.onKeyboardFocus { focused ->
    println("Text field is focused: $focused!")
  }

  textField.onKeyboardFocusEvent { focusEvent ->
    // If you need access to the original FocusEvent, use this expanded method variant.
    println("$this actor is focused: ${focusEvent.isFocused}!")
  }
}
```

Chaining actions with infix `then` function (`SequenceAction` utility) and infix `along` function
(`ParallelAction` utility):

```kotlin
import ktx.actors.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*

val sequence = alpha(0f) then fadeIn(1f) then delay(1f) then fadeOut(1f) 
actor += sequence // Adding action to the actor.

val parallel = fadeTo(0f) along scaleTo(0f, 0f) along moveTo(0f, 0f)
actor += parallel
```

Chaining actions with `+` operator (`SequenceAction` utility) and `/` operator (`ParallelAction` utility):

```kotlin
import ktx.actors.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*

val sequence = alpha(0f) + fadeIn(1f) + delay(1f) + fadeOut(1f)
actor += sequence // Adding action to the actor.

val parallel = fadeTo(0f) / scaleTo(0f, 0f) / moveTo(0f, 0f)
actor += parallel
```

Adding and removing actions to stages and actors with operators:
```kotlin
import ktx.actors.*

button += action
button -= otherAction

// Adding global Stage action:
stage += someAction 
// Since the action is added to Stage's root actor, it affects all widgets on the Stage.
```

Accessing and changing text of `Label` and `TextButton` widgets:

```kotlin
import ktx.actors.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton

val label = Label("text", skin)
label.txt // Returns "text".
label.txt = "new" // Changes current Label text to "new".

val button = TextButton("Click me!", skin)
button.txt // Returns "Click me!".
button.txt = "Drag me!" // Changes TextButton text to "Drag me!".
```

Extending `KtxInputListener`:

```kotlin
import ktx.actors.KtxInputListener

class MyInputListener : KtxInputListener() {
  // Implement the methods that handle events you plan to listen to:
  override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
    // Do something on mouse click.
    return true
  }
}
```

### Alternatives

- [VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI) includes some `Scene2D` utilities, as well as some extended
widgets to address some libGDX API problems. It is written in Java, though.
- [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) is a general purpose Guava-inspired libGDX Java utilities
library, which contain some `Scene2D` helpers.
- [LML](https://github.com/czyzby/gdx-lml/tree/master/lml) makes it easier to build `Scene2D` views thanks to its
HTML-inspired syntax.

#### Additional documentation

- [Scene2D UI article.](https://libgdx.com/wiki/graphics/2d/scene2d/scene2d-ui)
