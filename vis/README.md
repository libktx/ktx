[![VisUI](https://img.shields.io/badge/VisUI-1.4.2-blue.svg)](https://github.com/kotcrab/vis-ui)

# KTX: VisUI type-safe builders

Utilities for creating VisUI widgets using Kotlin type-safe builders.

### Why?

While LibGDX layout managers are simple enough to use directly in Kotlin or Java, their usage usually leads to overly
verbose code. GUI layouts presented in HTML, XML and other readable markup languages are easier to reason about than
cluttered Java code. Fortunately, Kotlin [type-safe builders](https://Kotlinlang.org/docs/reference/type-safe-builders.html)
allow to write DSL that is both as readable as markup languages and as powerful as Java.

### Guide

To start creating UI layout, call one of top level functions from `builder.kt` such as `table { }`, `verticalGroup { }`
or `gridGroup { }`. Every VisUI and LibGDX `WidgetGroup` has an equivalent method. By passing lambdas to these methods,
you can fully customize their content with extension functions for each `Actor`, as well as invoke any internal methods:

```Kotlin
val root = table {
  setFillParent(true)
  label("Hello, World!")
}
stage.addActor(root)
```

The closures get full access to all public methods that chosen `WidgetGroup` has. You can also create and immediately add 
new widgets to group simply by invoking methods from `WidgetFactory` interface, which is available from all type-safe 
builders.

#### Additional extensions

Consider using [ktx-actors](../actors) module to improve event handling with lambda-friendly extension methods like
`onChange` and `onClick`.

#### Note about `KWidgets`

In `ktx-vis` there are many utility widget classes starting with `K` followed by their original names. Those widgets
purpose is to provide syntax sugar for type-safe builders, and there is usually no need use them directly. In fact, all
factory methods for root actors already return the extended widgets where necessary to help you with GUI building.

#### Tooltips

`ktx-vis` provides extension methods for creating VisUI tooltips:
```Kotlin
import ktx.vis.*

label("Label with tooltip") {
  addTextTooltip("Tooltip text")
}
```

These methods include:

- `addTextTooltip` - adds simple text tooltip.
- `addTooltip` - adds tooltip with fully customized content.

#### Menus

`Menu` and `PopupMenu` instances are created in very similar way to UI layouts.

```Kotlin
import ktx.vis.*

val menu = popupMenu {
  menuItem("First Item")
  menuItem("Second Item")
  menuItem("Third Item") {
    subMenu {
      menuItem("SubMenu Item")
    }
  }
}
menu.showMenu(stage, 0f, 0f)
```

See examples section for `MenuBar` usage.

### Usage examples

Creating a `VisWindow`, immediately added to a `Stage`:

```Kotlin
import ktx.vis.*

stage.addActor(window("Window") {
  isModal = true
  label("Hello from Window")
})
```

Creating a `MenuBar`:

```Kotlin
import ktx.vis.*

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

![](img/menu.png)

Creating `ButtonGroup`:
```Kotlin
import ktx.vis.*

buttonTable {
    checkBox("First")
    checkBox("Second")
    checkBox("Third")
    buttonGroup.setMinCheckCount(1)
    buttonGroup.setMaxCheckCount(1)
}
```

`ButtonTable` is a specialized `Table` that adds all `Button` instances to internal `ButtonGroup`, allowing to keep
minimum and maximum counts of buttons checked at once.

Creating a `ButtonBar`:

```Kotlin
import ktx.vis.*
import com.kotcrab.vis.ui.widget.ButtonBar.ButtonType

buttonBar {
  setButton(ButtonType.APPLY, textButton("Accept"))
  setButton(ButtonType.CANCEL, textButton("Cancel"))
}
```

Creating `Tree` of `Label` instances:
```Kotlin
import ktx.scene2d.*

tree {
  label("Node")
  label("Node")
  label("Nest") { node ->
    node {
      label("Nested") { node ->
        node.label("Nested")
      }
      label("Nested")
    }
  }
  label("Node")
}
```
![Tree](img/tree.png)

Creating a `TabbedPane` with multiple tabs:

```Kotlin
import ktx.vis.*

table { cell ->
  cell.grow()

  tabbedPane("vertical", { cell ->
    cell.growY()
    tab("Tab1") {
      label("Inside tab 1")
    }
    tab("Tab2") {
      label("Inside tab 2")
    }
    tab("Tab3") {
      label("Inside tab 3")
    }
  }).apply {
    addTabContentsTo(table().cell(grow = true))
    //OR addTabContentsTo(container<Table>().cell(grow = true))
    switchTab(0)
  }
}
```

![](img/tabs.png)

Creating a form using `FormValidator`:

```Kotlin
import ktx.vis.*

table(true) {
  validator {
    defaults().left()

    label("Name: ")
    notEmpty(validatableTextField().cell(grow = true), "Name can't be empty")
    row()

    label("Age: ")
    val ageField = validatableTextField().cell(grow = true)
    notEmpty(ageField, "Age can't be empty")
    integerNumber(ageField, "Age must be number")
    valueGreaterThan(ageField, "You must be at least 18 years old", 18f, true)
    row()

    checked(checkBox("Accept terms").cell(colspan = 2), "You must accept terms")
    row()

    setMessageLabel(label("").cell(minWidth = 200f))
    addDisableTarget(textButton("Accept").cell(align = Align.right))
  }
}
```

![](img/form.png)

Creating a `ListView`:

```Kotlin
import ktx.vis.*
import com.badlogic.gdx.utils.Array as GdxArray
import com.kotcrab.vis.ui.util.adapter.AbstractListAdapter.SelectionMode
import com.kotcrab.vis.ui.util.adapter.SimpleListAdapter

table(true) {
  val osList = GdxArray<String>()
  osList.add("Windows")
  osList.add("Linux")
  osList.add("Mac")

  val adapter = SimpleListAdapter(osList)
  adapter.selectionMode = SelectionMode.SINGLE
  listView(adapter) {
    header = label("ListView header")
    footer = label("ListView footer")
  }
}
```

![](img/list.png)

Accessing `Cell` instances with`Table` children outside of building blocks:
```Kotlin
import ktx.vis.*
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label

table {
  val label: Label = label("Cell properties modified.").cell(expand = true)
  val cell: Cell<Label> = label("Wrapped in cell.").inCell
  val combined: Cell<Label> = label("Modified and wrapped.").cell(expand = true).inCell
  val afterBuildingBlock: Cell<Label> = label("Not limited to no init block actors.") {
    setWrap(true)
  }.cell(expand = true).inCell

  // Cells are available only for direct children of tables (or its extensions). 
  stack {
    // These would not compile:
    label("Invalid.").cell(expand = true)
    label("Invalid").inCell
  }
}
```

Accessing `Node` instances with `Table` children outside of building blocks (note that `KNode` is **KTX** custom wrapper
of LibGDX `Tree.Node` with additional building API support):
```Kotlin
import ktx.vis.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node

tree {
  val label: VisLabel = label("Node properties modified.").node(expanded = true)
  val cell: Node = label("Wrapped in node.").inNode
  val combined: Node = label("Modified and wrapped.").node(expanded = true).inNode
  val afterBuildingBlock: KNode = label("Not limited to no init block actors.") {
    setWrap(true)
  }.node(expanded = true).inNode
  // Nodes are available only for children of trees.
}
```


### Alternatives

- Creating layouts with [VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI) directly in Kotlin or Java.
- [LibGDX Markup Language](https://github.com/czyzby/gdx-lml/tree/master/lml) allows to build `Scene2D` views using
HTML-like syntax. It also features a [VisUI extension](https://github.com/czyzby/gdx-lml/tree/master/lml-vis). However,
it lacks first-class Kotlin support and the flexibility of a powerful programming language.

#### Additional documentation

- [VisUI wiki.](https://github.com/kotcrab/vis-editor/wiki/VisUI)
- [Scene2D article.](https://github.com/libgdx/libgdx/wiki/Scene2d)
- [Scene2D UI article.](https://github.com/libgdx/libgdx/wiki/Scene2d.ui)
- [`Table` article.](https://github.com/libgdx/libgdx/wiki/Table)

