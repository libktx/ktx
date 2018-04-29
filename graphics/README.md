# KTX : Graphics utilities

This module is a wrapper over LibGDX graphics utilities.

### Guide

#### ShapeRenderer

Add methods to use `ShapeRenderer` class with `Vector2` and `Vector3` classes
when LibGDX exposes methods with x, y or z args.

For example: 

```
val position = Vector2(1f, 0f)  
val batch = ShapeRenderer()
// ...
batch.circle(position, 5f)  
```   