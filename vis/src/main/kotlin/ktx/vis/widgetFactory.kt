package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.layout.FloatingGroup
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup
import com.kotcrab.vis.ui.layout.VerticalFlowGroup
import com.kotcrab.vis.ui.widget.*

/** @author Kotcrab */

interface WidgetFactory<R> {
  companion object {
    val DEFAULT_STYLE = "default"
  }

  // Non-parental widgets

  fun label(text: String, styleName: String = DEFAULT_STYLE, init: VisLabel.() -> Unit = {}): R
      = actor(VisLabel(text, styleName), init)

  fun linkLabel(text: String, url: String = text, styleName: String = DEFAULT_STYLE, init: LinkLabel.() -> Unit = {}): R
      = actor(LinkLabel(text, url, styleName), init)

  fun image(drawable: Drawable, scaling: Scaling = Scaling.stretch, align: Int = Align.center,
            init: VisImage.() -> Unit = {}): R = actor(VisImage(drawable, scaling, align), init)

  fun image(drawableName: String, init: VisImage.() -> Unit = {}): R = actor(VisImage(drawableName), init)

  fun <T> list(styleName: String = DEFAULT_STYLE, init: VisList<T>.() -> Unit = {}): R = actor(VisList<T>(styleName), init)

  fun progressBar(min: Float = 0f, max: Float = 100f, step: Float = 1f, vertical: Boolean = false,
                  styleName: String = DEFAULT_STYLE, init: VisProgressBar.() -> Unit = {}): R
      = actor(VisProgressBar(min, max, step, vertical, styleName), init)

  fun <T> selectBox(styleName: String = DEFAULT_STYLE, init: VisSelectBox<T>.() -> Unit = {}): R
      = actor(VisSelectBox<T>(styleName), init)

  fun slider(min: Float = 0f, max: Float = 100f, step: Float = 1f, vertical: Boolean = false,
             styleName: String = DEFAULT_STYLE, init: VisSlider.() -> Unit = {}): R
      = actor(VisSlider(min, max, step, vertical, styleName), init)

  fun textArea(text: String = "", styleName: String = DEFAULT_STYLE, init: VisTextArea.() -> Unit = {}): R
      = actor(VisTextArea(text, styleName), init)

  fun highlightTextArea(text: String = "", styleName: String = DEFAULT_STYLE, init: HighlightTextArea.() -> Unit = {}): R =
      actor(HighlightTextArea(text, styleName), init)

  fun scrollableTextArea(text: String = "", styleName: String = DEFAULT_STYLE, init: ScrollableTextArea.() -> Unit = {}): R =
      actor(ScrollableTextArea(text, styleName), init)

  fun textField(text: String = "", styleName: String = DEFAULT_STYLE, init: VisTextField.() -> Unit = {}): R
      = actor(VisTextField(text, styleName), init)

  fun validatableTextField(text: String = "", styleName: String = DEFAULT_STYLE, init: VisValidatableTextField.() -> Unit = {}): R
      = actor(VisValidatableTextField(text, styleName), init)

  fun touchpad(deadzoneRadius: Float, styleName: String = DEFAULT_STYLE, init: Touchpad.() -> Unit = {}): R
      = actor(Touchpad(deadzoneRadius, VisUI.getSkin(), styleName), init)

  fun busyBar(styleName: String = DEFAULT_STYLE, init: BusyBar.() -> Unit = {}): R
      = actor(BusyBar(styleName), init)

  fun separator(styleName: String = DEFAULT_STYLE, init: Separator.() -> Unit = {}): R = actor(Separator(styleName), init)

  // Parental widgets

  fun button(styleName: String = DEFAULT_STYLE, init: KButton.() -> Unit = {}): R
      = actor(KButton(styleName), init)

  fun textButton(text: String, styleName: String = DEFAULT_STYLE, init: KVisTextButton.() -> Unit = {}): R
      = actor(KVisTextButton(text, styleName), init)

  fun imageButton(styleName: String = DEFAULT_STYLE, init: KVisImageButton.() -> Unit = {}): R
      = actor(KVisImageButton(styleName), init)

  fun imageButton(imageUp: Drawable, imageDown: Drawable = imageUp, imageChecked: Drawable = imageUp,
                  init: KVisImageButton.() -> Unit = {}): R
      = actor(KVisImageButton(imageUp, imageDown, imageChecked), init)

  fun imageTextButton(text: String, styleName: String = DEFAULT_STYLE, init: KVisImageTextButton.() -> Unit = {}): R
      = actor(KVisImageTextButton(text, styleName), init)

  fun checkBox(text: String, styleName: String = DEFAULT_STYLE, init: KVisCheckBox.() -> Unit = {}): R
      = actor(KVisCheckBox(text, styleName), init)

  fun radioButton(text: String, init: KVisRadioButton.() -> Unit = {}): R
      = actor(KVisRadioButton(text), init)

  fun tree(styleName: String = DEFAULT_STYLE, init: KVisTree.() -> Unit = {}): R = actor(KVisTree(styleName), init)

  // Parental widgets groups

  fun table(defaultSpacing: Boolean = false, init: KVisTable.() -> Unit = {}): R = actor(KVisTable(defaultSpacing), init)

  fun horizontalGroup(init: KHorizontalGroup.() -> Unit = {}): R = actor(KHorizontalGroup(), init)

  fun horizontalFlowGroup(spacing: Float = 0f, init: KHorizontalFlowGroup.() -> Unit = {}): R
      = actor(KHorizontalFlowGroup(spacing), init)

  fun verticalGroup(init: KVerticalGroup.() -> Unit = {}): R = actor(KVerticalGroup(), init)

  fun verticalFlowGroup(spacing: Float = 0f, init: KVerticalFlowGroup.() -> Unit = {}): R
      = actor(KVerticalFlowGroup(spacing), init)

  fun gridGroup(itemSize: Float = 256f, spacing: Float = 8f, init: KGridGroup.() -> Unit): R
      = actor(KGridGroup(itemSize, spacing), init)

  fun floatingGroup(init: KFloatingGroup.() -> Unit): R = actor(KFloatingGroup(), init)

  fun floatingGroup(prefWidth: Float, prefHeight: Float, init: KFloatingGroup.() -> Unit): R = actor(KFloatingGroup(), init)

  fun stack(init: KStack.() -> Unit): R = actor(KStack(), init)

  fun <T : Actor> actor(actor: T, init: T.() -> Unit): R {
    actor.init()
    return addActorToWidgetGroup(actor)
  }

  fun addActorToWidgetGroup(actor: Actor): R
}

@Suppress("UNCHECKED_CAST")
interface TableWidgetFactory : WidgetFactory<Cell<*>> {
  // Non-parental widgets

  override fun label(text: String, styleName: String, init: VisLabel.() -> Unit): Cell<VisLabel>
      = super.label(text, styleName, init) as Cell<VisLabel>

  override fun linkLabel(text: String, url: String, styleName: String, init: LinkLabel.() -> Unit): Cell<LinkLabel>
      = super.linkLabel(text, url, styleName, init) as Cell<LinkLabel>

  override fun image(drawable: Drawable, scaling: Scaling, align: Int, init: VisImage.() -> Unit): Cell<VisImage>
      = super.image(drawable, scaling, align, init) as Cell<VisImage>

  override fun image(drawableName: String, init: VisImage.() -> Unit): Cell<VisImage>
      = super.image(drawableName, init) as Cell<VisImage>

  override fun <T> list(styleName: String, init: VisList<T>.() -> Unit): Cell<VisList<T>>
      = super.list(styleName, init) as Cell<VisList<T>>

  override fun progressBar(min: Float, max: Float, step: Float, vertical: Boolean, styleName: String, init: VisProgressBar.() -> Unit): Cell<VisProgressBar>
      = super.progressBar(min, max, step, vertical, styleName, init) as Cell<VisProgressBar>

  override fun <T> selectBox(styleName: String, init: VisSelectBox<T>.() -> Unit): Cell<VisSelectBox<T>>
      = super.selectBox(styleName, init) as Cell<VisSelectBox<T>>

  override fun slider(min: Float, max: Float, step: Float, vertical: Boolean, styleName: String, init: VisSlider.() -> Unit): Cell<VisSlider>
      = super.slider(min, max, step, vertical, styleName, init) as Cell<VisSlider>

  override fun textArea(text: String, styleName: String, init: VisTextArea.() -> Unit): Cell<VisTextArea>
      = super.textArea(text, styleName, init) as Cell<VisTextArea>

  override fun highlightTextArea(text: String, styleName: String, init: HighlightTextArea.() -> Unit): Cell<HighlightTextArea>
      = super.highlightTextArea(text, styleName, init) as Cell<HighlightTextArea>

  override fun scrollableTextArea(text: String, styleName: String, init: ScrollableTextArea.() -> Unit): Cell<ScrollableTextArea>
      = super.scrollableTextArea(text, styleName, init) as Cell<ScrollableTextArea>

  override fun textField(text: String, styleName: String, init: VisTextField.() -> Unit): Cell<VisTextField>
      = super.textField(text, styleName, init) as Cell<VisTextField>

  override fun validatableTextField(text: String, styleName: String, init: VisValidatableTextField.() -> Unit): Cell<VisValidatableTextField>
      = super.validatableTextField(text, styleName, init) as Cell<VisValidatableTextField>

  override fun touchpad(deadzoneRadius: Float, styleName: String, init: Touchpad.() -> Unit): Cell<Touchpad>
      = super.touchpad(deadzoneRadius, styleName, init) as Cell<Touchpad>

  override fun busyBar(styleName: String, init: BusyBar.() -> Unit): Cell<BusyBar>
      = super.busyBar(styleName, init) as Cell<BusyBar>

  override fun separator(styleName: String, init: Separator.() -> Unit): Cell<Separator>
      = super.separator(styleName, init) as Cell<Separator>

  override fun <T : Actor> actor(actor: T, init: T.() -> Unit): Cell<T> = super.actor(actor, init) as Cell<T>

  // Parental widgets

  override fun button(styleName: String, init: KButton.() -> Unit): Cell<Button> {
    return super.button(styleName, init) as Cell<Button>
  }

  override fun textButton(text: String, styleName: String, init: KVisTextButton.() -> Unit): Cell<VisTextButton> {
    return super.textButton(text, styleName, init) as Cell<VisTextButton>
  }

  override fun imageButton(styleName: String, init: KVisImageButton.() -> Unit): Cell<VisImageButton> {
    return super.imageButton(styleName, init) as Cell<VisImageButton>
  }

  override fun imageButton(imageUp: Drawable, imageDown: Drawable, imageChecked: Drawable, init: KVisImageButton.() -> Unit): Cell<VisImageButton> {
    return super.imageButton(imageUp, imageDown, imageChecked, init) as Cell<VisImageButton>
  }

  override fun imageTextButton(text: String, styleName: String, init: KVisImageTextButton.() -> Unit): Cell<VisImageTextButton> {
    return super.imageTextButton(text, styleName, init) as Cell<VisImageTextButton>
  }

  override fun checkBox(text: String, styleName: String, init: KVisCheckBox.() -> Unit): Cell<VisCheckBox> {
    return super.checkBox(text, styleName, init) as Cell<VisCheckBox>
  }

  override fun radioButton(text: String, init: KVisRadioButton.() -> Unit): Cell<VisRadioButton> {
    return super.radioButton(text, init) as Cell<VisRadioButton>
  }

  override fun tree(styleName: String, init: KVisTree.() -> Unit): Cell<VisTree> {
    return super.tree(styleName, init) as Cell<VisTree>
  }

  // Parental widgets groups

  override fun table(defaultSpacing: Boolean, init: KVisTable.() -> Unit): Cell<VisTable> {
    return super.table(defaultSpacing, init) as Cell<VisTable>
  }

  override fun horizontalGroup(init: KHorizontalGroup.() -> Unit): Cell<HorizontalGroup> {
    return super.horizontalGroup(init) as Cell<HorizontalGroup>
  }

  override fun horizontalFlowGroup(spacing: Float, init: KHorizontalFlowGroup.() -> Unit): Cell<HorizontalFlowGroup> {
    return super.horizontalFlowGroup(spacing, init) as Cell<HorizontalFlowGroup>
  }

  override fun verticalGroup(init: KVerticalGroup.() -> Unit): Cell<VerticalGroup> {
    return super.verticalGroup(init) as Cell<VerticalGroup>
  }

  override fun verticalFlowGroup(spacing: Float, init: KVerticalFlowGroup.() -> Unit): Cell<VerticalFlowGroup> {
    return super.verticalFlowGroup(spacing, init) as Cell<VerticalFlowGroup>
  }

  override fun gridGroup(itemSize: Float, spacing: Float, init: KGridGroup.() -> Unit): Cell<GridGroup> {
    return super.gridGroup(itemSize, spacing, init) as Cell<GridGroup>
  }

  override fun floatingGroup(init: KFloatingGroup.() -> Unit): Cell<FloatingGroup> {
    return super.floatingGroup(init) as Cell<FloatingGroup>
  }

  override fun floatingGroup(prefWidth: Float, prefHeight: Float, init: KFloatingGroup.() -> Unit): Cell<FloatingGroup> {
    return super.floatingGroup(prefWidth, prefHeight, init) as Cell<FloatingGroup>
  }

  override fun stack(init: KStack.() -> Unit): Cell<Stack> {
    return super.stack(init) as Cell<Stack>
  }
}

@Suppress("UNCHECKED_CAST")
interface WidgetGroupWidgetFactory : WidgetFactory<Any> {
  // Non-parental widgets

  override fun label(text: String, styleName: String, init: VisLabel.() -> Unit): VisLabel = super.label(text, styleName, init) as VisLabel

  override fun linkLabel(text: String, url: String, styleName: String, init: LinkLabel.() -> Unit): LinkLabel
      = super.linkLabel(text, url, styleName, init) as LinkLabel

  override fun image(drawable: Drawable, scaling: Scaling, align: Int, init: VisImage.() -> Unit): VisImage
      = super.image(drawable, scaling, align, init) as VisImage

  override fun image(drawableName: String, init: VisImage.() -> Unit): VisImage
      = super.image(drawableName, init) as VisImage

  override fun <T> list(styleName: String, init: VisList<T>.() -> Unit): VisList<T>
      = super.list(styleName, init) as VisList<T>

  override fun progressBar(min: Float, max: Float, step: Float, vertical: Boolean, styleName: String, init: VisProgressBar.() -> Unit): VisProgressBar
      = super.progressBar(min, max, step, vertical, styleName, init) as VisProgressBar

  override fun <T> selectBox(styleName: String, init: VisSelectBox<T>.() -> Unit): VisSelectBox<T>
      = super.selectBox(styleName, init) as VisSelectBox<T>

  override fun slider(min: Float, max: Float, step: Float, vertical: Boolean, styleName: String, init: VisSlider.() -> Unit): VisSlider
      = super.slider(min, max, step, vertical, styleName, init) as VisSlider

  override fun textArea(text: String, styleName: String, init: VisTextArea.() -> Unit): VisTextArea
      = super.textArea(text, styleName, init) as VisTextArea

  override fun highlightTextArea(text: String, styleName: String, init: HighlightTextArea.() -> Unit): HighlightTextArea
      = super.highlightTextArea(text, styleName, init) as HighlightTextArea

  override fun scrollableTextArea(text: String, styleName: String, init: ScrollableTextArea.() -> Unit): ScrollableTextArea
      = super.scrollableTextArea(text, styleName, init) as ScrollableTextArea

  override fun textField(text: String, styleName: String, init: VisTextField.() -> Unit): VisTextField
      = super.textField(text, styleName, init) as VisTextField

  override fun validatableTextField(text: String, styleName: String, init: VisValidatableTextField.() -> Unit): VisValidatableTextField
      = super.validatableTextField(text, styleName, init) as VisValidatableTextField

  override fun touchpad(deadzoneRadius: Float, styleName: String, init: Touchpad.() -> Unit): Touchpad
      = super.touchpad(deadzoneRadius, styleName, init) as Touchpad

  override fun busyBar(styleName: String, init: BusyBar.() -> Unit): BusyBar
      = super.busyBar(styleName, init) as BusyBar

  override fun separator(styleName: String, init: Separator.() -> Unit): Separator
      = super.separator(styleName, init) as Separator

  override fun <T : Actor> actor(actor: T, init: T.() -> Unit): T = super.actor(actor, init) as T

  // Parental widgets

  override fun button(styleName: String, init: KButton.() -> Unit): Button {
    return super.button(styleName, init) as Button
  }

  override fun textButton(text: String, styleName: String, init: KVisTextButton.() -> Unit): VisTextButton {
    return super.textButton(text, styleName, init) as VisTextButton
  }

  override fun imageButton(styleName: String, init: KVisImageButton.() -> Unit): VisImageButton {
    return super.imageButton(styleName, init) as VisImageButton
  }

  override fun imageButton(imageUp: Drawable, imageDown: Drawable, imageChecked: Drawable, init: KVisImageButton.() -> Unit): VisImageButton {
    return super.imageButton(imageUp, imageDown, imageChecked, init) as VisImageButton
  }

  override fun imageTextButton(text: String, styleName: String, init: KVisImageTextButton.() -> Unit): VisImageTextButton {
    return super.imageTextButton(text, styleName, init) as VisImageTextButton
  }

  override fun checkBox(text: String, styleName: String, init: KVisCheckBox.() -> Unit): VisCheckBox {
    return super.checkBox(text, styleName, init) as VisCheckBox
  }

  override fun radioButton(text: String, init: KVisRadioButton.() -> Unit): VisRadioButton {
    return super.radioButton(text, init) as VisRadioButton
  }

  override fun tree(styleName: String, init: KVisTree.() -> Unit): VisTree {
    return super.tree(styleName, init) as VisTree
  }

  // Parental widgets groups

  override fun table(defaultSpacing: Boolean, init: KVisTable.() -> Unit): VisTable {
    return super.table(defaultSpacing, init) as VisTable
  }

  override fun horizontalGroup(init: KHorizontalGroup.() -> Unit): HorizontalGroup {
    return super.horizontalGroup(init) as HorizontalGroup
  }

  override fun horizontalFlowGroup(spacing: Float, init: KHorizontalFlowGroup.() -> Unit): HorizontalFlowGroup {
    return super.horizontalFlowGroup(spacing, init) as HorizontalFlowGroup
  }

  override fun verticalGroup(init: KVerticalGroup.() -> Unit): VerticalGroup {
    return super.verticalGroup(init) as VerticalGroup
  }

  override fun verticalFlowGroup(spacing: Float, init: KVerticalFlowGroup.() -> Unit): VerticalFlowGroup {
    return super.verticalFlowGroup(spacing, init) as VerticalFlowGroup
  }

  override fun gridGroup(itemSize: Float, spacing: Float, init: KGridGroup.() -> Unit): GridGroup {
    return super.gridGroup(itemSize, spacing, init) as GridGroup
  }

  override fun floatingGroup(init: KFloatingGroup.() -> Unit): FloatingGroup {
    return super.floatingGroup(init) as FloatingGroup
  }

  override fun floatingGroup(prefWidth: Float, prefHeight: Float, init: KFloatingGroup.() -> Unit): FloatingGroup {
    return super.floatingGroup(prefWidth, prefHeight, init) as FloatingGroup
  }

  override fun stack(init: KStack.() -> Unit): Stack {
    return super.stack(init) as Stack
  }
}
