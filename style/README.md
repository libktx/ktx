[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-style.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-style)

# KTX: style builders

Type-safe builders of official Scene2D widget styles.

### Why?

While creating widget styles in Java is possible, it usually turns out to be too verbose. LibGDX provides an alternative -
`Skin` class and its JSON loader. Thanks to reflection, you can define your styles in a single concise JSON file and load
them at runtime. This is fine most of the time, but this approach does have its issues: the two obvious ones being
reflection usage and no validation during writing. You basically find out about your typos at runtime, as there seems
to be no schema that the JSON could be validated against and no official editors with code completion. JSON format also
suffers from no extension mechanism, which leads to data duplication.

Kotlin type-safe builders can make style definitions less verbose than usual, as easily readable as JSON and basically
as fast to parse as hand-written Java code thanks to inlined functions and no reflection usage. By converting your JSON
skins into `ktx-style` builders, you speed up your application startup time.
 
### Guide

`Skin` instances can be constructed with `skin` functions, both of which accept a Kotlin-style init block.

You can quickly extract assets from `Skin` using the `skin.get<DesiredClass>("resourceName")` syntax. When the compiler
can "guess" the type of variable, this can be shortened even further into `skin["resourceName"]`. This method can be
used for all kinds of resources that can be stored in a `Skin`.

`get` and `set` operator functions were added. `Skin` assets can now be accessed with brace operators:

```Kotlin
val skin = Skin()
skin["name"] = BitmapFont()
val font: BitmapFont = skin["name"]
```

Note that both of these functions use reified generics, so they need to be able to "extract" the variable type from
context. For example, `val font = skin["name"]` would not compile, as the compiler would not be able to guess that
instance of `BitmapFont` class is requested.

Additional methods were also added to leverage type inference and skip `Class` parameters:

```Kotlin
val res: Resource? = skin.optional("name")
val found = skin.has<Resource>("name")

skin.add(resource, "name")
skin += otherResource  // Uses the "default" name

skin.remove<Resource>("name")

val map: ObjectMap<String, Resource>? = skin.getAll()
```

These extension methods and operators include:
* `get` (square bracket operator): returns a resource from the `Skin` or throws an exception.
* `optional`: returns `null` or a resource from the `Skin` if it exists.
* `set` (square bracket operator): assigns a resource to the `Skin`.
* `add`: assigns a resource to the `Skin`.
* `plusAssign` (`+=` operator): assigns a resource with the default style to the `Skin`.
* `remove`: removes a resource from the `Skin`.
* `has`: checks is the `Skin` contains a resource.
* `getAll`: returns all resources of the selected type.

Note that all `name` parameters can also be skipped to use the default style name, `"default"`.

An extension method for every style of every Scene2D widget was added to `Skin`. Each method name matches `lowerCamelCase`
name of the actor class. For example, the method used to create `ScrollPaneStyle` instances is named `scrollPane`.
Signature of every extension method is pretty much the same - they consume 3 parameters: style name (defaults to
`"default"`), optional name of extended style and an init block, which is usually passed as a Kotlin lambda. If a name
of existing style name is given as the `extend` parameter, the new style will copy its properties.

Currently supported extension methods include:

`Skin` method | Style class
:---: | ---
`color` | `com.badlogic.gdx.graphics.Color`
`button` | `com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle`
`checkBox` | `com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle`
`imageButton` | `com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle`
`imageTextButton` | `com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle`
`label` | `com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle`
`list` | `com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle`
`progressBar` | `com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle`
`scrollPane` | `com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle`
`selectBox` | `com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle`
`slider` | `com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle`
`splitPaneStyle` | `com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle`
`textButton` | `com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle`
`textField` | `com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle`
`textTooltip` | `com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle`
`touchpad` | `com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle`
`tree` | `com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle`
`window` | `com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle`

### Usage examples

Creating a new empty `Skin`:
```Kotlin
import ktx.style.*

val skin = skin {
  // Customize skin here.
}
```

Creating a new `Skin` with drawables extracted from a `TextureAtlas`:
```Kotlin
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ktx.style.*

val skin = skin(TextureAtlas(Gdx.files.internal("skin.atlas"))) {
  // Customize skin here.
  // Tip: ktx-assets could make the TextureAtlas loading much nicer.
}
```

Extending an existing `Skin`:
```Kotlin
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.style.*

val skin = Skin()

// All style builders are regular extension methods,
// so they can be used directly on a `Skin` instance:
skin.label {
  // Define your label style here.
}

// Style definitions can also be wrapped in a block such as apply:
skin.apply {
  button {
    // Define your button style here.
  }
}
```

Creating a new `LabelStyle` with `"default"` name:
```Kotlin
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.style.*

skin {
  label {
    font = BitmapFont()
    fontColor = Color.WHITE
  }
}
```

Defining colors:
```Kotlin
import ktx.style.*

skin {
  color("white", 1f, 1f, 1f, 1f)
  color("black", red = 0f, green = 0f, blue = 0f)
  color("red", red = 1f, green = 0f, blue = 0f, alpha = 1f)
  // Note: last argument (alpha) is optional and defaults to 1f.
}
```

Creating a new `ButtonStyle` named `"default"` with drawables extracted from the atlas:
```Kotlin
import ktx.style.*

skin(myAtlas) {
  // Skin is available under `it` parameter, so you can access other resources.
  button { 
    up = it["buttonUp"] // Automatically extracts drawable with buttonUp name.
    down = it["buttonDown"]
  }
}
```

Creating a new `ButtonStyle` named `"toggle"` that extends style with `"default"` name (it inherits its properties and
allows to override them):
```Kotlin
import ktx.style.*

skin(myAtlas) {
  button {
    up = it["buttonUp"]
    down = it["buttonDown"]
  }
  button("toggle", extend = defaultStyle) {
    checked = it["buttonChecked"]
  }
}
```

Reusing an existing style - passing a `LabelStyle` instance to `TooltipStyle`:
```Kotlin
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ktx.style.*

skin(myAtlas) {
  val labelStyle = label {
    font = BitmapFont()
    fontColor = Color.WHITE
  }
  textTooltip {
    label = labelStyle // or it[defaultStyle]
    background = it["tooltipBackground"]
  }
}
```

Nested style definitions with renamed `skin` parameter - creating a `LabelStyle` and a `Color` on demand to customize
`TooltipStyle` (all three resources will be available in the skin afterwards):
```Kotlin
import com.badlogic.gdx.graphics.Color
import ktx.style.*

skin(myAtlas) { skin ->
  textTooltip {
    label = skin.label("tooltipText") {
      font = skin["arial"]
      fontColor = skin.color("black", 0f, 0f, 0f)
    }
    background = skin["tooltipBackground"]
  }
}
```

Extracting resources from the skin - getting instances of previously created styles for `SelectBoxStyle`:
```Kotlin
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import ktx.style.*

skin {
  scrollPane("selectScroll") {
    // Customize ScrollPane...
  }
  list("selectList") {
    // Customize ListStyle...
  }
  selectBox {
    scrollStyle = it["selectScroll"]
    listStyle = it["selectList"]
    // Note: generics are mostly optional, as Kotlin is smart enough to guess the type of
    // requested asset and insert its class automatically. This applies to most assets like
    // fonts, colors, styles, drawables and texture regions.
  }
}
```

Adding custom widget style with similar Kotlin builder syntax:
```Kotlin
import ktx.style.*

skin {
  addStyle("customStyleName", YourCustomWidgetStyle()) {
    customProperty = true
  }
}
```

#### Implementation tip: type-safe style assets

As long as you use strings for the IDs, its hard to call the API truly type-safe. After all, what's stopping you from
trying to extract a `Drawable` or `Color` that does not exist? Same applies to creating actors: they usually consume
a `String` parameter as style name and it's not validated at compile time if the style _actually exists_. However, if
you're willing to put some extra effort into styles building and keep a certain programming rule, you can get fully
type-safe GUI building with ease.

The secret is to keep all your expected data in enums and (over)use Kotlin syntax sugar to make the code as natural as
possible. For example, take a look at these `ButtonStyle` definitions:

```Kotlin
button {
  up = it["buttonUp"]
  down = it["buttonDown"]
}
button("toggle", extend = defaultStyle) {
  checked = it["buttonChecked"]
}
```

Since they use plain strings as drawable names and there's no magic going on, we can safely assume that these could be
converted into enum values - preferably listing _all_ drawables in the atlas:

```Kotlin
package your.company

enum class Drawables {
  buttonUp,
  buttonDown,
  buttonChecked; // Add all drawables from TextureAtlas.
}
```

Along with a static import, this brings our type-safe boilerplate to a pleasant minimum.

While we're at it, it makes sense to list all styles with non-default
name to provide validation when invoking actor constructors:

```Kotlin
package your.company

enum class Buttons {
  toggle; // Add all non-default ButtonStyle names.

  operator fun invoke() = toString()
}
```

`invoke` operator above allows to, well, _invoke_ enum instances like any function to obtain their name -
for example: `Buttons.toggle()`. 

Let's sum it up and refactor the `ButtonStyle` definitions:

```Kotlin
import ktx.style.*
import your.company.Buttons
import your.company.Drawables.*

skin(myAtlas) {
  button {
    up = it[buttonUp]
    down = it[buttonDown]
  }
  button(Buttons.toggle(), extend = defaultStyle) {
    checked = it[buttonChecked]
  }
}
```

What's best about it, enums do not necessarily make your code _longer_ or less readable - all while having the 
advantage of powerful code completion of your IDE of choice and validation at compile time. As long as you 
don't need to create assets at runtime with custom unpredictable IDs, we encourage you to store your drawables,
fonts, colors and non-default styles names as enums to ensure complete safely at compile time.

The advantage of using an `enum` over a "standard" singleton (`object`) with `String` properties or `String` 
constants is that you can easily extract a list of all values from an `enum`, while getting all fields from
an object or constants from a package is not trivial.

#### Synergy

[`ktx-assets`](../assets) or [`ktx-assets-async`](../assets-async) might prove useful for loading and management
of `Skin` assets including `Textures` and `TextureAtlases`.

### Alternatives

- Default LibGDX JSON skin loading mechanism allows to customize `Skin` instances thanks to reflection. Apart from the
issues listed in the introduction, it does have an advantage over type-safe builders: skins can be reloaded without
recompilation of the application, allowing for GUI tweaks without restarting. While a certainly useful feature during
prototyping phase, it also requires the developer to prepare `Skin` reloading and GUI rebuilding code to make it work.
- [USL](https://github.com/kotcrab/VisEditor/wiki/USL) is a DSL that compiles to LibGDX JSON skin files. While similar
to JSON in structure, it adds some more features like packages handling and style inheritance. Furthermore, it features
no runtime overhead, as it is translated to plain skin JSON data. Its style inheritance mechanism might prove more
flexible than `ktx-style`, as you can extend styles even if they do not share the same class. However, since it relies
on LibGDX JSON skin loading (based on reflection) and currently contains no editor capable of code completion, it still
suffers from the same issues as regular JSON.

#### Additional documentation

- [`Skin` article.](https://github.com/libgdx/libgdx/wiki/Skin)
