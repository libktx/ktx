# KTX: VisUI style builders

Type-safe builders of **VisUI** widget styles.

This is an extension of [`ktx-style`](../style) module. See its documentation to get started with type-safe stylesheet
builders for `Scene2D` widgets.

Additionally to features provided by the `ktx-style` library, `ktx-style-vis` comes with factory methods for most
[VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI) widget styles.

_Implementation note_: `FileChooserStyle` is not included, as it is basically a desktop-only widget that would not even
work (or compile - see GWT) on most platforms. Adding a similar extension method for `FileChooserStyle` would be pretty
straightforward: see `visStyle.kt` for code samples.

#### Additional documentation

- [VisUI wiki.](https://github.com/kotcrab/vis-editor/wiki/VisUI)
- [`Skin` article.](https://github.com/libgdx/libgdx/wiki/Skin)
