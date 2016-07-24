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
import com.kotcrab.vis.ui.widget.color.BasicColorPicker
import com.kotcrab.vis.ui.widget.color.ExtendedColorPicker
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.kotcrab.vis.ui.widget.spinner.SpinnerModel

/** @author Kotcrab */

const val DEFAULT_STYLE = "default"

/**
 * Provides methods allowing to build scene2d.ui using type-safe builders.
 * @see [TableWidgetFactory]
 * @see [WidgetGroupWidgetFactory]
 */
interface WidgetFactory<R> {
  // Non-parental widgets

  /** @see [VisLabel] */
  fun label(text: String, styleName: String = DEFAULT_STYLE, init: VisLabel.() -> Unit = {}): R
      = actor(VisLabel(text, styleName), init)

  /** @see [LinkLabel] */
  fun linkLabel(text: String, url: String = text, styleName: String = DEFAULT_STYLE, init: LinkLabel.() -> Unit = {}): R
      = actor(LinkLabel(text, url, styleName), init)

  /** @see [VisImage] */
  fun image(drawable: Drawable, scaling: Scaling = Scaling.stretch, align: Int = Align.center,
            init: VisImage.() -> Unit = {}): R = actor(VisImage(drawable, scaling, align), init)

  /** @see [VisImage] */
  fun image(drawableName: String, init: VisImage.() -> Unit = {}): R = actor(VisImage(drawableName), init)

  /** @see [VisList] */
  fun <T> list(styleName: String = DEFAULT_STYLE, init: VisList<T>.() -> Unit = {}): R = actor(VisList<T>(styleName), init)

  /** @see [VisProgressBar] */
  fun progressBar(min: Float = 0f, max: Float = 100f, step: Float = 1f, vertical: Boolean = false, init: VisProgressBar.() -> Unit = {}): R
      = actor(VisProgressBar(min, max, step, vertical), init)

  /** @see [VisProgressBar] */
  fun progressBar(min: Float = 0f, max: Float = 100f, step: Float = 1f, vertical: Boolean = false,
                  styleName: String, init: VisProgressBar.() -> Unit = {}): R
      = actor(VisProgressBar(min, max, step, vertical, styleName), init)

  /** @see [VisSelectBox] */
  fun <T> selectBox(styleName: String = DEFAULT_STYLE, init: VisSelectBox<T>.() -> Unit = {}): R
      = actor(VisSelectBox<T>(styleName), init)

  /** @see [VisSlider] */
  fun slider(min: Float = 0f, max: Float = 100f, step: Float = 1f, vertical: Boolean = false, init: VisSlider.() -> Unit = {}): R
      = actor(VisSlider(min, max, step, vertical), init)

  /** @see [VisSlider] */
  fun slider(min: Float = 0f, max: Float = 100f, step: Float = 1f, vertical: Boolean = false, styleName: String, init: VisSlider.() -> Unit = {}): R
      = actor(VisSlider(min, max, step, vertical, styleName), init)

  /** @see [VisTextArea] */
  fun textArea(text: String = "", styleName: String = DEFAULT_STYLE, init: VisTextArea.() -> Unit = {}): R
      = actor(VisTextArea(text, styleName), init)

  /** @see [HighlightTextArea] */
  fun highlightTextArea(text: String = "", styleName: String = DEFAULT_STYLE, init: HighlightTextArea.() -> Unit = {}): R =
      actor(HighlightTextArea(text, styleName), init)

  /** @see [ScrollableTextArea] */
  fun scrollableTextArea(text: String = "", styleName: String = DEFAULT_STYLE, init: ScrollableTextArea.() -> Unit = {}): R =
      actor(ScrollableTextArea(text, styleName), init)

  /** @see [VisTextField] */
  fun textField(text: String = "", styleName: String = DEFAULT_STYLE, init: VisTextField.() -> Unit = {}): R
      = actor(VisTextField(text, styleName), init)

  /** @see [VisValidatableTextField] */
  fun validatableTextField(text: String = "", styleName: String = DEFAULT_STYLE, init: VisValidatableTextField.() -> Unit = {}): R
      = actor(VisValidatableTextField(text, styleName), init)

  /** @see [Touchpad] */
  fun touchpad(deadzoneRadius: Float, styleName: String = DEFAULT_STYLE, init: Touchpad.() -> Unit = {}): R
      = actor(Touchpad(deadzoneRadius, VisUI.getSkin(), styleName), init)

  /** @see [BusyBar] */
  fun busyBar(styleName: String = DEFAULT_STYLE, init: BusyBar.() -> Unit = {}): R
      = actor(BusyBar(styleName), init)

  /** @see [Separator] */
  fun separator(styleName: String = DEFAULT_STYLE, init: Separator.() -> Unit = {}): R = actor(Separator(styleName), init)

  // Parental widgets

  /** @see [Button] */
  fun button(styleName: String = DEFAULT_STYLE, init: KButton.() -> Unit = {}): R
      = actor(KButton(styleName), init)

  /** @see [VisTextButton] */
  fun textButton(text: String, styleName: String = DEFAULT_STYLE, init: KVisTextButton.() -> Unit = {}): R
      = actor(KVisTextButton(text, styleName), init)

  /** @see [VisImageButton] */
  fun imageButton(styleName: String = DEFAULT_STYLE, init: KVisImageButton.() -> Unit = {}): R
      = actor(KVisImageButton(styleName), init)

  /** @see [VisImageButton] */
  fun imageButton(imageUp: Drawable, imageDown: Drawable = imageUp, imageChecked: Drawable = imageUp,
                  init: KVisImageButton.() -> Unit = {}): R
      = actor(KVisImageButton(imageUp, imageDown, imageChecked), init)

  /** @see [VisImageTextButton] */
  fun imageTextButton(text: String, styleName: String = DEFAULT_STYLE, init: KVisImageTextButton.() -> Unit = {}): R
      = actor(KVisImageTextButton(text, styleName), init)

  /** @see [VisCheckBox] */
  fun checkBox(text: String, styleName: String = DEFAULT_STYLE, init: KVisCheckBox.() -> Unit = {}): R
      = actor(KVisCheckBox(text, styleName), init)

  /** @see [VisRadioButton] */
  fun radioButton(text: String, init: KVisRadioButton.() -> Unit = {}): R
      = actor(KVisRadioButton(text), init)

  /** @see [VisTree] */
  fun tree(styleName: String = DEFAULT_STYLE, init: KVisTree.() -> Unit = {}): R = actor(KVisTree(styleName), init)

  /** @see [BasicColorPicker] */
  fun basicColorPicker(styleName: String = DEFAULT_STYLE, init: KBasicColorPicker.() -> Unit = {}): R
      = actor(KBasicColorPicker(styleName), init)

  /** @see [ExtendedColorPicker] */
  fun extendedColorPicker(styleName: String = DEFAULT_STYLE, init: KExtendedColorPicker.() -> Unit = {}): R
      = actor(KExtendedColorPicker(styleName), init)

  /** @see [Spinner] */
  fun spinner(name: String, model: SpinnerModel, styleName: String = DEFAULT_STYLE, init: KSpinner.() -> Unit = {}): R
      = actor(KSpinner(styleName, name, model), init)

  // Parental widgets groups

  /** @see [VisTable] */
  fun table(defaultSpacing: Boolean = false, init: KVisTable.() -> Unit): R = actor(KVisTable(defaultSpacing), init)

  /** @see [HorizontalGroup] */
  fun horizontalGroup(init: KHorizontalGroup.() -> Unit): R = actor(KHorizontalGroup(), init)

  /** @see [HorizontalFlowGroup] */
  fun horizontalFlowGroup(spacing: Float = 0f, init: KHorizontalFlowGroup.() -> Unit): R
      = actor(KHorizontalFlowGroup(spacing), init)

  /** @see [VerticalGroup] */
  fun verticalGroup(init: KVerticalGroup.() -> Unit): R = actor(KVerticalGroup(), init)

  /** @see [VerticalFlowGroup] */
  fun verticalFlowGroup(spacing: Float = 0f, init: KVerticalFlowGroup.() -> Unit): R
      = actor(KVerticalFlowGroup(spacing), init)

  /** @see [GridGroup] */
  fun gridGroup(itemSize: Float = 256f, spacing: Float = 8f, init: KGridGroup.() -> Unit): R
      = actor(KGridGroup(itemSize, spacing), init)

  /** @see [FloatingGroup] */
  fun floatingGroup(init: KFloatingGroup.() -> Unit): R = actor(KFloatingGroup(), init)

  /** @see [FloatingGroup] */
  fun floatingGroup(prefWidth: Float, prefHeight: Float, init: KFloatingGroup.() -> Unit): R
      = actor(KFloatingGroup(prefWidth, prefHeight), init)

  /** @see [Stack] */
  fun stack(init: KStack.() -> Unit): R = actor(KStack(), init)

  // Others

  /** @see [VisScrollPane] */
  fun scrollPane(widget: Actor, styleName: String = DEFAULT_STYLE, init: VisScrollPane.() -> Unit = {}): R
      = actor(VisScrollPane(widget, styleName), init)

  /** @see [VisSplitPane] */
  fun splitPane(firstWidget: Actor, secondWidget: Actor, vertical: Boolean = false, init: VisSplitPane.() -> Unit = {}): R
      = actor(VisSplitPane(firstWidget, secondWidget, vertical), init)

  /** @see [VisSplitPane] */
  fun splitPane(firstWidget: Actor, secondWidget: Actor, vertical: Boolean = false, styleName: String, init: VisSplitPane.() -> Unit = {}): R
      = actor(VisSplitPane(firstWidget, secondWidget, vertical, styleName), init)

  /** @see [MultiSplitPane] */
  fun multiSplitPane(vertical: Boolean = false, init: MultiSplitPane.() -> Unit = {}): R
      = actor(MultiSplitPane(vertical), init)

  /** @see [MultiSplitPane] */
  fun multiSplitPane(vertical: Boolean = false, styleName: String, init: MultiSplitPane.() -> Unit = {}): R
      = actor(MultiSplitPane(vertical, styleName), init)

  /** @see [Container] */
  fun <T : Actor> container(actor: T, init: Container<T>.() -> Unit = {}): R = actor(Container<T>(actor), init)

  /** @see [CollapsibleWidget] */
  fun collapsible(table: Table, init: CollapsibleWidget.() -> Unit = {}): R = actor(CollapsibleWidget(table), init)

  /** @see [Actor] */
  fun <T : Actor> actor(actor: T, init: T.() -> Unit): R {
    actor.init()
    return addActorToWidgetGroup(actor)
  }

  fun addActorToWidgetGroup(actor: Actor): R
}

@Suppress("UNCHECKED_CAST")
/**
 * Implemented by classes providing type-safe builders. Requires that [addActorToWidgetGroup]
 * returns [Cell] instance with actor being added to [Table].
 */
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

  override fun progressBar(min: Float, max: Float, step: Float, vertical: Boolean, init: VisProgressBar.() -> Unit): Cell<VisProgressBar>
      = super.progressBar(min, max, step, vertical, init) as Cell<VisProgressBar>

  override fun progressBar(min: Float, max: Float, step: Float, vertical: Boolean, styleName: String, init: VisProgressBar.() -> Unit): Cell<VisProgressBar>
      = super.progressBar(min, max, step, vertical, styleName, init) as Cell<VisProgressBar>

  override fun <T> selectBox(styleName: String, init: VisSelectBox<T>.() -> Unit): Cell<VisSelectBox<T>>
      = super.selectBox(styleName, init) as Cell<VisSelectBox<T>>

  override fun slider(min: Float, max: Float, step: Float, vertical: Boolean, init: VisSlider.() -> Unit): Cell<VisSlider>
      = super.slider(min, max, step, vertical, init) as Cell<VisSlider>

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

  // Parental widgets

  override fun button(styleName: String, init: KButton.() -> Unit): Cell<Button>
      = super.button(styleName, init) as Cell<Button>

  override fun textButton(text: String, styleName: String, init: KVisTextButton.() -> Unit): Cell<VisTextButton>
      = super.textButton(text, styleName, init) as Cell<VisTextButton>

  override fun imageButton(styleName: String, init: KVisImageButton.() -> Unit): Cell<VisImageButton>
      = super.imageButton(styleName, init) as Cell<VisImageButton>

  override fun imageButton(imageUp: Drawable, imageDown: Drawable, imageChecked: Drawable, init: KVisImageButton.() -> Unit): Cell<VisImageButton>
      = super.imageButton(imageUp, imageDown, imageChecked, init) as Cell<VisImageButton>

  override fun imageTextButton(text: String, styleName: String, init: KVisImageTextButton.() -> Unit): Cell<VisImageTextButton>
      = super.imageTextButton(text, styleName, init) as Cell<VisImageTextButton>

  override fun checkBox(text: String, styleName: String, init: KVisCheckBox.() -> Unit): Cell<VisCheckBox>
      = super.checkBox(text, styleName, init) as Cell<VisCheckBox>

  override fun radioButton(text: String, init: KVisRadioButton.() -> Unit): Cell<VisRadioButton>
      = super.radioButton(text, init) as Cell<VisRadioButton>

  override fun tree(styleName: String, init: KVisTree.() -> Unit): Cell<VisTree>
      = super.tree(styleName, init) as Cell<VisTree>

  override fun basicColorPicker(styleName: String, init: KBasicColorPicker.() -> Unit): Cell<BasicColorPicker>
      = super.basicColorPicker(styleName, init) as Cell<BasicColorPicker>

  override fun extendedColorPicker(styleName: String, init: KExtendedColorPicker.() -> Unit): Cell<ExtendedColorPicker>
      = super.extendedColorPicker(styleName, init) as Cell<ExtendedColorPicker>

  override fun spinner(name: String, model: SpinnerModel, styleName: String, init: KSpinner.() -> Unit): Cell<Spinner>
      = super.spinner(name, model, styleName, init) as Cell<Spinner>

  // Parental widgets groups

  override fun table(defaultSpacing: Boolean, init: KVisTable.() -> Unit): Cell<VisTable>
      = super.table(defaultSpacing, init) as Cell<VisTable>

  override fun horizontalGroup(init: KHorizontalGroup.() -> Unit): Cell<HorizontalGroup>
      = super.horizontalGroup(init) as Cell<HorizontalGroup>

  override fun horizontalFlowGroup(spacing: Float, init: KHorizontalFlowGroup.() -> Unit): Cell<HorizontalFlowGroup>
      = super.horizontalFlowGroup(spacing, init) as Cell<HorizontalFlowGroup>

  override fun verticalGroup(init: KVerticalGroup.() -> Unit): Cell<VerticalGroup>
      = super.verticalGroup(init) as Cell<VerticalGroup>

  override fun verticalFlowGroup(spacing: Float, init: KVerticalFlowGroup.() -> Unit): Cell<VerticalFlowGroup>
      = super.verticalFlowGroup(spacing, init) as Cell<VerticalFlowGroup>

  override fun gridGroup(itemSize: Float, spacing: Float, init: KGridGroup.() -> Unit): Cell<GridGroup>
      = super.gridGroup(itemSize, spacing, init) as Cell<GridGroup>

  override fun floatingGroup(init: KFloatingGroup.() -> Unit): Cell<FloatingGroup>
      = super.floatingGroup(init) as Cell<FloatingGroup>

  override fun floatingGroup(prefWidth: Float, prefHeight: Float, init: KFloatingGroup.() -> Unit): Cell<FloatingGroup>
      = super.floatingGroup(prefWidth, prefHeight, init) as Cell<FloatingGroup>

  override fun stack(init: KStack.() -> Unit): Cell<Stack>
      = super.stack(init) as Cell<Stack>

  // Others

  override fun scrollPane(widget: Actor, styleName: String, init: VisScrollPane.() -> Unit): Cell<VisScrollPane>
      = super.scrollPane(widget, styleName, init) as Cell<VisScrollPane>

  override fun splitPane(firstWidget: Actor, secondWidget: Actor, vertical: Boolean, styleName: String, init: VisSplitPane.() -> Unit): Cell<VisSplitPane>
      = super.splitPane(firstWidget, secondWidget, vertical, styleName, init) as Cell<VisSplitPane>

  override fun multiSplitPane(vertical: Boolean, styleName: String, init: MultiSplitPane.() -> Unit): Cell<MultiSplitPane>
      = super.multiSplitPane(vertical, styleName, init) as Cell<MultiSplitPane>

  override fun <T : Actor> container(actor: T, init: Container<T>.() -> Unit): Cell<Container<T>>
      = super.container(actor, init) as Cell<Container<T>>

  override fun collapsible(table: Table, init: CollapsibleWidget.() -> Unit): Cell<CollapsibleWidget>
      = super.collapsible(table, init) as Cell<CollapsibleWidget>

  override fun <T : Actor> actor(actor: T, init: T.() -> Unit): Cell<T>
      = super.actor(actor, init) as Cell<T>
}

@Suppress("UNCHECKED_CAST")
/**
 * Implemented by widget classes providing type-safe builders. Requires that [addActorToWidgetGroup] returns [Actor]
 * instance being added to [WidgetGroup].
 */
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

  override fun progressBar(min: Float, max: Float, step: Float, vertical: Boolean, init: VisProgressBar.() -> Unit): VisProgressBar
      = super.progressBar(min, max, step, vertical, init) as VisProgressBar

  override fun <T> selectBox(styleName: String, init: VisSelectBox<T>.() -> Unit): VisSelectBox<T>
      = super.selectBox(styleName, init) as VisSelectBox<T>

  override fun slider(min: Float, max: Float, step: Float, vertical: Boolean, styleName: String, init: VisSlider.() -> Unit): VisSlider
      = super.slider(min, max, step, vertical, styleName, init) as VisSlider

  override fun slider(min: Float, max: Float, step: Float, vertical: Boolean, init: VisSlider.() -> Unit): VisSlider
      = super.slider(min, max, step, vertical, init) as VisSlider

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

  // Parental widgets

  override fun button(styleName: String, init: KButton.() -> Unit): Button
      = super.button(styleName, init) as Button

  override fun textButton(text: String, styleName: String, init: KVisTextButton.() -> Unit): VisTextButton
      = super.textButton(text, styleName, init) as VisTextButton

  override fun imageButton(styleName: String, init: KVisImageButton.() -> Unit): VisImageButton
      = super.imageButton(styleName, init) as VisImageButton

  override fun imageButton(imageUp: Drawable, imageDown: Drawable, imageChecked: Drawable, init: KVisImageButton.() -> Unit): VisImageButton
      = super.imageButton(imageUp, imageDown, imageChecked, init) as VisImageButton

  override fun imageTextButton(text: String, styleName: String, init: KVisImageTextButton.() -> Unit): VisImageTextButton
      = super.imageTextButton(text, styleName, init) as VisImageTextButton

  override fun checkBox(text: String, styleName: String, init: KVisCheckBox.() -> Unit): VisCheckBox
      = super.checkBox(text, styleName, init) as VisCheckBox

  override fun radioButton(text: String, init: KVisRadioButton.() -> Unit): VisRadioButton
      = super.radioButton(text, init) as VisRadioButton

  override fun tree(styleName: String, init: KVisTree.() -> Unit): VisTree
      = super.tree(styleName, init) as VisTree

  override fun basicColorPicker(styleName: String, init: KBasicColorPicker.() -> Unit): BasicColorPicker
      = super.basicColorPicker(styleName, init) as BasicColorPicker

  override fun extendedColorPicker(styleName: String, init: KExtendedColorPicker.() -> Unit): ExtendedColorPicker
      = super.extendedColorPicker(styleName, init) as ExtendedColorPicker

  override fun spinner(name: String, model: SpinnerModel, styleName: String, init: KSpinner.() -> Unit): Spinner
      = super.spinner(name, model, styleName, init) as Spinner

  // Parental widgets groups

  override fun table(defaultSpacing: Boolean, init: KVisTable.() -> Unit): VisTable
      = super.table(defaultSpacing, init) as VisTable

  override fun horizontalGroup(init: KHorizontalGroup.() -> Unit): HorizontalGroup
      = super.horizontalGroup(init) as HorizontalGroup

  override fun horizontalFlowGroup(spacing: Float, init: KHorizontalFlowGroup.() -> Unit): HorizontalFlowGroup
      = super.horizontalFlowGroup(spacing, init) as HorizontalFlowGroup

  override fun verticalGroup(init: KVerticalGroup.() -> Unit): VerticalGroup
      = super.verticalGroup(init) as VerticalGroup

  override fun verticalFlowGroup(spacing: Float, init: KVerticalFlowGroup.() -> Unit): VerticalFlowGroup
      = super.verticalFlowGroup(spacing, init) as VerticalFlowGroup

  override fun gridGroup(itemSize: Float, spacing: Float, init: KGridGroup.() -> Unit): GridGroup
      = super.gridGroup(itemSize, spacing, init) as GridGroup

  override fun floatingGroup(init: KFloatingGroup.() -> Unit): FloatingGroup
      = super.floatingGroup(init) as FloatingGroup

  override fun floatingGroup(prefWidth: Float, prefHeight: Float, init: KFloatingGroup.() -> Unit): FloatingGroup
      = super.floatingGroup(prefWidth, prefHeight, init) as FloatingGroup

  override fun stack(init: KStack.() -> Unit): Stack = super.stack(init) as Stack

  // Others

  override fun scrollPane(widget: Actor, styleName: String, init: VisScrollPane.() -> Unit): VisScrollPane
      = super.scrollPane(widget, styleName, init) as VisScrollPane

  override fun splitPane(firstWidget: Actor, secondWidget: Actor, vertical: Boolean, styleName: String, init: VisSplitPane.() -> Unit): VisSplitPane
      = super.splitPane(firstWidget, secondWidget, vertical, styleName, init) as VisSplitPane

  override fun multiSplitPane(vertical: Boolean, styleName: String, init: MultiSplitPane.() -> Unit): MultiSplitPane
      = super.multiSplitPane(vertical, styleName, init) as MultiSplitPane

  override fun <T : Actor> container(actor: T, init: Container<T>.() -> Unit): Container<T>
      = super.container(actor, init) as Container<T>

  override fun collapsible(table: Table, init: CollapsibleWidget.() -> Unit): CollapsibleWidget
      = super.collapsible(table, init) as CollapsibleWidget

  override fun <T : Actor> actor(actor: T, init: T.() -> Unit): T = super.actor(actor, init) as T
}
