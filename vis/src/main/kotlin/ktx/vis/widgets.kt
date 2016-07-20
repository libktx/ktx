package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.layout.FloatingGroup
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup
import com.kotcrab.vis.ui.layout.VerticalFlowGroup
import com.kotcrab.vis.ui.widget.*

/** @author Kotcrab */

class KVisTable : VisTable, TableWidgetFactory {
  constructor() : super()
  constructor(setVisDefaults: Boolean) : super(setVisDefaults)

  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

class KButton : Button, TableWidgetFactory {
  constructor() : super()
  constructor(styleName: String) : super(VisUI.getSkin(), styleName)
  constructor(style: ButtonStyle) : super(style)

  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

class KVisCheckBox : VisCheckBox, TableWidgetFactory {
  constructor(text: String) : super(text)
  constructor(text: String, styleName: String) : super(text, styleName)

  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

class KVisRadioButton : VisRadioButton, TableWidgetFactory {
  constructor(text: String) : super(text)

  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

class KVisTextButton : VisTextButton, TableWidgetFactory {
  constructor(text: String) : super(text)
  constructor(text: String, styleName: String) : super(text, styleName)

  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

class KVisImageButton : VisImageButton, TableWidgetFactory {
  constructor(imageUp: Drawable) : super(imageUp)
  constructor(imageUp: Drawable, imageDown: Drawable) : super(imageUp, imageDown)
  constructor(imageUp: Drawable, imageDown: Drawable, imageChecked: Drawable) : super(imageUp, imageDown, imageChecked)
  constructor(styleName: String) : super(styleName)

  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

class KVisImageTextButton : VisImageTextButton, TableWidgetFactory {
  constructor(text: String, imageUp: Drawable) : super(text, imageUp)
  constructor(text: String, styleName: String, imageUp: Drawable) : super(text, styleName, imageUp)
  constructor(text: String, styleName: String, imageUp: Drawable, imageDown: Drawable) : super(text, styleName, imageUp, imageDown)
  constructor(text: String, styleName: String) : super(text, styleName)

  override fun addActorToWidgetGroup(actor: Actor): Cell<*> = add(actor)
}

class KVisTree : VisTree, WidgetGroupWidgetFactory {
  constructor() : super()
  constructor(styleName: String) : super(styleName)

  override fun addActorToWidgetGroup(actor: Actor): Any {
    addActor(actor)
    return actor
  }
}

class KStack : Stack, WidgetGroupWidgetFactory {
  constructor() : super()
  constructor(vararg actors: Actor) : super(*actors)

  override fun addActorToWidgetGroup(actor: Actor): Any {
    addActor(actor)
    return actor
  }
}

class KHorizontalGroup : HorizontalGroup(), WidgetGroupWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Any {
    addActor(actor)
    return actor
  }
}

class KHorizontalFlowGroup : HorizontalFlowGroup, WidgetGroupWidgetFactory {
  constructor() : super()
  constructor(spacing: Float) : super(spacing)

  override fun addActorToWidgetGroup(actor: Actor): Any {
    addActor(actor)
    return actor
  }
}

class KVerticalGroup : VerticalGroup(), WidgetGroupWidgetFactory {
  override fun addActorToWidgetGroup(actor: Actor): Any {
    addActor(actor)
    return actor
  }
}

class KVerticalFlowGroup : VerticalFlowGroup, WidgetGroupWidgetFactory {
  constructor() : super()
  constructor(spacing: Float) : super(spacing)

  override fun addActorToWidgetGroup(actor: Actor): Any {
    addActor(actor)
    return actor
  }
}

class KGridGroup : GridGroup, WidgetGroupWidgetFactory {
  constructor() : super()
  constructor(itemSize: Float) : super(itemSize)
  constructor(itemSize: Float, spacing: Float) : super(itemSize, spacing)

  override fun addActorToWidgetGroup(actor: Actor): Any {
    addActor(actor)
    return actor
  }
}

class KFloatingGroup : FloatingGroup, WidgetGroupWidgetFactory {
  constructor() : super()
  constructor(prefWidth: Float, prefHeight: Float) : super(prefWidth, prefHeight)

  override fun addActorToWidgetGroup(actor: Actor): Any {
    addActor(actor)
    return actor
  }
}
