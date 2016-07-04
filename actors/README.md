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
- Lambda-compatible `Actor.onScrollFocus` method was added. Allows to listen to `FocusEvents` with `scroll` type.
- Lambda-compatible `Actor.onKeyboardFocus` method was added. Allows to listen to `FocusEvents` with `keyboard` type.

#### Actions

- Global actions can be added and removed from `Stage` with `+` and `-` operators.
- `Action.then` *infix* extension function allows to easily create action sequences with pleasant syntax.

### Alternatives

- [VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI) includes some `Scene2D` utilities, as well as some extended
widgets to address some of the LibGDX API problems. It is written in Java, though.
- [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) is a general purpose Guava-inspired LibGDX Java utilities
library, which contain some `Scene2D` helpers.
- [LibGDX Markup Language](https://github.com/czyzby/gdx-lml/tree/master/lml) makes it easier to build `Scene2D` views
thanks to its HTML-inspired syntax.
