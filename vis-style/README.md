# KTX: VisUI style builders

Type-safe builders of VisUI widget styles.

See [`ktx-style`](../style) documentation for more informations about this module. Additionally to features provided by
the mentioned library, `ktx-style-vis` provides factory methods for VisUI widget styles, allowing to build type-safe
GUI stylesheets similarly to `ktx-style`.

Implementation note: `FileChooserStyle` is not included, as it is basically a desktop-only widget that would not even
work (or compile - see GWT) on most platforms. Adding a similar extension method for `FileChooserStyle` would be pretty
straightforward: see `visStyle.kt` for code samples.
