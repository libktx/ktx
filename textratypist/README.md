[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-textratypist.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-textratypist)

# KTX: `TextraTypist` type-safe builders

Utilities for creating [`TextraTypist`](https://github.com/tommyettinger/textratypist) animated text labels using
Kotlin type-safe builders.

## Why?

`TextraTypist` provides animated typewriter-style text rendering for Scene2D.
It’s powerful for dialog boxes, narration, RPG text, terminals, UI hints, and comic-style text effects.

`ktx-textratypist` integrates Textra widgets directly into the existing `ktx-scene2d` DSL.

## Getting Started

```kotlin
import ktx.scene2d.*
import ktx.textratypist.*

val label = typingLabel(text = "Hello, world!")
```

TextraTypist requires a `Skin` with compatible styles for its widgets. This can be handled easily by using a `FWSkin`:

```kotlin
Scene2DSkin.defaultSkin = FWSkin(Gdx.files.internal("my-skin.json"))
```

## Available Builders

| Widget        | DSL Method                       | Description                                      |
|---------------|----------------------------------|--------------------------------------------------|
| `TextraLabel` | `textraLabel(text, skin, style)` | Plain labels with effects and styles             |
| `TypingLabel` | `typingLabel(text, skin, style)` | Labels with typing animation, effects and styles |

## Usage Examples

### Basic TypingLabel

```kotlin
scene2d.table {
    typingLabel("Welcome to [RED]KTX Textratypist[]!") {
        textSpeed = 0.5F
    }
}
```

### Using TextraTypist Markup Tags

Textratypist provides an immense variety of markup tags to create text effects and styles. Effects use curly braces
`{}` by default, while styles use square brackets `[]`.

| Effect             | Description                                                                                                                              | Example Usage                |
|--------------------|------------------------------------------------------------------------------------------------------------------------------------------|------------------------------|
| {RESET}            | Set all formatting and speed changes to default using `{RESET}`.                                                                         | `{FAST}Normal text{RESET}`   |
| Slow / Fast typing | Sets speed. Use `{SLOW}`, `{FAST}`, `{SLOWER}`, `{FASTER}`, etc.                                                                         | `{FASTER}Quick text{RESET}`  |
| Special effects    | Set [special effects](https://github.com/tommyettinger/textratypist/wiki/Tokens#special-effects): `{RAINBOW}`, `{SHAKE}`, `{WAVE}`, etc. | `{WAVE}Wavy text{RESET}`     |
| []                 | Undo the last change to style/color/formatting.                                                                                          | `[RED]Red text[]`            |
| [ ]                | Resets all style/color/formatting.                                                                                                       | `[*][RED]Bold red text[ ]`   |
| Color tags         | Set text color using `[COLORNAME]` or `[#RGB88]`.                                                                                        | `[GREEN]Green text[]`        |
| Bold               | Set bold formatting using `[*]` or `[BOLD]`.                                                                                             | `[*]Bold text[]`             |
| Italic             | Set italic formatting using `[/]` or `[ITALIC]`.                                                                                         | `[/]Italic text[]`           |
| Underline          | Set underline formatting using `[_]` or `[UNDERLINE]`.                                                                                   | `[_]Underlined text[]`       |
| Superscript        | Set superscript formatting using `[^]` or `[SUPERSCRIPT]`.                                                                               | `Some text [^]Superscript[]` |

Example:

```kotlin
typingLabel("{WAVE}System reboot[] in [YELLOW]{SLOWER}3... 2... 1...[]")
```

The possibilities are nearly endless! See the [TextraTypist Wiki](https://github.com/tommyettinger/textratypist/wiki) for a full list of supported tags and features.

### Synergy

| Library       | Purpose                   |
|---------------|---------------------------|
| `ktx-scene2d` | Layout + widget DSL       |
| `ktx-actors`  | Stage helpers & utilities |

### Alternatives:

| Library                                                   | Purpose                                                                    |
|-----------------------------------------------------------|----------------------------------------------------------------------------|
| [`typing-label`](https://github.com/rafaskb/typing-label) | Provides a LibGdx typing label. TextraTypist is/was based on this library- |
