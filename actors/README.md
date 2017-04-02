# KTX: general `Scene2D` utilities

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
- `Group` and `Stage` support actor adding and removal through `+` and `-` operators.
- `Stage.contains(Actor)` method was added to support `in` operator. This will report `true` if the `Actor` is on the
`Stage` (it does not have to be a direct child of `Stage` root group).
- `Actor.alpha` and `Stage.alpha` mock-up fields were added to support easy modification of `Color.a` value.
- `Actor.setKeyboardFocus` and `.setScrollFocus` allow to quickly (un)focus the actor on its stage.

#### Event listeners

- Lambda-compatible `Actor.onChange` method was added. Allows to listen to `ChangeEvents`.
- Lambda-compatible `Actor.onClick` methods were added. (More verbose version has access to local coordinates of the click
events.) Attaches `ClickListeners`.
- Lambda-compatible `Actor.onKey` method was added. Allows to listen to `InputEvents` with `keyTyped` type.
- Lambda-compatible `Actor.onKeyDown` and `Actor.onKeyUp` methods were added. They allow to listen to `InputEvents`
with `keyDown` and `keyUp` type, consuming key code of the pressed or released key (see LibGDX `Keys` class).
- Lambda-compatible `Actor.onScrollFocus` method was added. Allows to listen to `FocusEvents` with `scroll` type.
- Lambda-compatible `Actor.onKeyboardFocus` method was added. Allows to listen to `FocusEvents` with `keyboard` type.
- `KtxInputListener` is an open class that extends `InputListener` with no-op default implementations and type
improvements (nullability data).

#### Actions

- Global actions can be added and removed from `Stage` with `+` and `-` operators.
- `Action.then` *infix* extension function allows to easily create action sequences with pleasant syntax.

#### Widgets

- `txt` extension property added to `Label` and `TextButton` widgets. Since types of `getText` and `setText`
methods in both of this widgets are not compatible (get returns `StringBuilder`, set consumes a `CharSequence`), an
extension was necessary to let these widgets fully benefit from idiomatic Kotlin properties syntax. Since Kotlin
properties cannot overshadow Java methods, property was renamed to still hopefully readable `txt`.

### Usage examples

Centering actor on a stage:
```Kotlin
import ktx.actors.*

window.centerPosition()
```

Adding and removing actors with operators:
```Kotlin
import ktx.actors.*

table + button
table - label
stage + table
```

Checking if actor is on a stage or in a group:
```Kotlin
import ktx.actors.*

button in stage
button in table
```

Quickly accessing actor and stage alpha color value:
```Kotlin
import ktx.actors.*

label.alpha = 0.5f
stage.alpha = 0.2f
```

Focusing events on actors:
```Kotlin
import ktx.actors.*

textField.setKeyboardFocus(true)
scrollPane.setScrollFocus(false)
```

Adding event listeners:
```Kotlin
import ktx.actors.*

button.onChange { changeEvent, button ->
  println("$button changed!") }
  
label.onClick { inputEvent, label ->
  println("$label clicked!") }
table.onClick { inputEvent, table, x, y ->
  println("$table clicked at (${x}, ${y})!") }
  
textField.onKey { inputEvent, textField, key ->
  println("Typed $key char in ${textField}!") }

scrollPane.onScrollFocus { focusEvent, scrollPane ->
  println("$scrollPane focus: ${event.isFocused}!") }
textField.onKeyboardFocus { focusEvent, textField ->
  println("$textField focus: ${event.isFocused}!") }
```

Chaining actions (`SequenceAction` utility):
```Kotlin
import ktx.actors.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*

val sequence = alpha(0f) then fadeIn(1f) then delay(1f) then fadeOut(1f)
actor + sequence // Adding action to the actor.
```

Adding and removing actions to stages and actors with operators:
```Kotlin
import ktx.actors.*

button + action - otherAction
stage + someAction // Adds action to stage root actor,
                   // affecting all actors on the stage.
```

Accessing and changing text of `Label` and `TextButton` widgets:

```Kotlin
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

```Kotlin
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
widgets to address some of the LibGDX API problems. It is written in Java, though.
- [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) is a general purpose Guava-inspired LibGDX Java utilities
library, which contain some `Scene2D` helpers.
- [LibGDX Markup Language](https://github.com/czyzby/gdx-lml/tree/master/lml) makes it easier to build `Scene2D` views
thanks to its HTML-inspired syntax.

#### Additional documentation

- [Scene2D UI article.](https://github.com/libgdx/libgdx/wiki/Scene2d.ui)
