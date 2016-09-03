# KTX: VisUI type-safe builders

Utilities for creating VisUI widgets using Kotlin type-safe builders.

### Why?

While libGDX layout managers are simple enough to use directly in Kotlin or Java it leads to cluttered and too verbose code. 
Fortunately we can improve it by using [Kotlin type-safe builders](https://kotlinlang.org/docs/reference/type-safe-builders.html).

Consider using [ktx-actors](https://github.com/czyzby/ktx/tree/master/actors) module to improve event handling using extension 
methods like `onChange` and `onClick`.

### Guide

To start creating UI layout you call one of top level function from `builder.kt` such as `table { }`, `verticalGroup { }`, `gridGroup { }`
and so on. Every VisUI and libGDX WidgetGroup has equivalent method. Those method returns `WidgetGroup` instance
that you can add to other `WidgetGroup` or in case of root table directly to Stage:
```kotlin
val root = table {
  setFillParent(true)
  label("Hello, World!")
}
stage.addActor(root)
```

Inside closure you get access to all methods that created `WidgetGroup` provides. You can also create and immediately add 
new widgets to group simply by invoking methods from `WidgetFactory` interface, which is available from all type-safe 
builders. Every libGDX and VisUI widget and `WidgetGroup` has equivalent method, for example 
`label`, `textButton`, `textField`, `table`, `verticalGroup` etc.

In libGDX `WidgetGroups` can be divided into two types: `Table`-like where you use `add()` to append new widget to group, you
get `Cell<Actor>` in return. The rest of `WidgetGroups` uses `addActor` and does not return anything. Access to 
`Cell` when adding actors to `Table` is very important because it provides all methods to customize layout. Ktx-vis handles 
this division in very transparent way: when you are creating widgets inside `table { }` closure all factory methods will 
return `Cell<ActorType>`. When you are inside `verticalGroup { }`, `gridGroup { }` or other `WidgetGroup` factory methods 
will return just `ActorType`. Example:
```kotlin
table {
  val cell = label("") 
  val actor = label("").actor // extracting VisLabel from Cell 
  label("").grow() // change returned Cell property
}

verticalGroup { 
  val label = label("") // inside standard WidgetGroup just created actor is returned
}
```

#### A note about KWidgets
In ktx-vis there are many widgets that starts with `K` followed by their original names. Those widgets purpose is to provide
interface for using type-safe builders, there is no need use them directly. In fact all APIs will return widgets
with their normal type however inside closures you will have access to them since they by implementing `WidgetFactory`
interface allows to utilize type-safe builders.

#### Tooltips
Ktx-vis provides extension methods for creating VisUI tooltips:
```kotlin
label("Label with tooltip") {
  addTextTooltip("Tooltip text")
}
```
- `addTextTooltip` adds text tooltip
- `addTooltip` adds tooltip with custom content

#### Menus

Menus and PopupMenus are created in very similar way to UI layouts.
```kotlin
val menu = popupMenu {
  menuItem("First Item")
  menuItem("Second Item")
  menuItem("Third Item") {
    subMenu {
      menuItem("SubMenu Item")
    }
  }
}
//...
menu.showMenu(stage, 0f, 0f)
```
See examples section for `MenuBar` usage.

### Usage examples

Creating `VisWindow`:
```kotlin
stage.addActor(window("Window") {
  isModal = true
  label("Hello from Window")
})
```

Creating a `MenuBar`:
```kotlin
val menuBar = menuBar {
  menu("File") {
    menuItem("New") {
      subMenu {
        menuItem("Project")
        menuItem("Module")
        menuItem("File")
      }
    }
    menuItem("Open") { /**/ }
  }
  menu ("Edit") {
    menuItem("Undo") {
      setShortcut(Keys.CONTROL_LEFT, Keys.Z)
    }
    menuItem("Undo") { /**/ }
  }
}
rootTable.add(menuBar.table).top().growX().row()
```
![](http://dl.kotcrab.com/github/ktx/menu.png)

Creating `ButtonGroup`:
```kotlin
buttonTable {
    checkBox("First")
    checkBox("Second")
    checkBox("Third")
    buttonGroup.setMinCheckCount(1)
    buttonGroup.setMaxCheckCount(1)
}
```
`ButtonTable` is a specialized `Table` that adds all `Buttons` to internal `ButtonGroup`

Creating `ButtonBar`:
```kotlin
buttonBar {
  setButton(APPLY, textButton("Accept"))
  setButton(CANCEL, textButton("Cancel"))
}
```

Creating a `TabbedPane` with tabs:
```kotlin
table {
  tabbedPane("vertical") {
    tab("Tab1") {
      label("Inside tab 1")
    }
    tab("Tab2") {
      label("Inside tab 2")
    }
    tab("Tab3") {
      label("Inside tab 3")
    }

    addTabContentsTo(table().grow())
    //OR addTabContentsTo(container<Table>().grow())

    switchTab(0)
  }.cell.growY()
}
```
![](http://dl.kotcrab.com/github/ktx/tabs.png)

Creating form using `FormValidator`:
```kotlin
table(true) {
  validator {
    defaults().left()

    label("Name: ")
    notEmpty(validatableTextField().grow().actor, "Name can't be empty")
    row()

    label("Age: ")
    val ageField = validatableTextField().grow().actor
    notEmpty(ageField, "Age can't be empty")
    integerNumber(ageField, "Age must be number")
    valueGreaterThan(ageField, "You must be at least 18 years old", 18f, true)
    row()

    checked(checkBox("Accept terms").colspan(2).actor, "You must accept terms")
    row()

    table(true) {
      setMessageLabel(label("").width(200f).actor)
      addDisableTarget(textButton("Accept").right().actor)
    }.colspan(2)
  }
}
```
![](http://dl.kotcrab.com/github/ktx/form.png)

Creating `ListView`:
```kotlin
import com.badlogic.gdx.utils.Array as GdxArray
//...

table(true) {
  val osList = GdxArray<String>()
  osList.add("Windows")
  osList.add("Linux")
  osList.add("Mac")

  val adapter = SimpleListAdapter(osList)
  adapter.selectionMode = SINGLE
  listView(adapter) {
    header = label("ListView header")
    footer = label("ListView footer")
  }
}
```
![](http://dl.kotcrab.com/github/ktx/list.png)


### Alternatives

- [LibGDX Markup Language](https://github.com/czyzby/gdx-lml/tree/master/lml) allows to build `Scene2D` views using HTML-like syntax.
- Creating layout directly in Kotlin or Java
