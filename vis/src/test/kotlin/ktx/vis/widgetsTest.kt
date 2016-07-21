package ktx.vis

import org.junit.runner.RunWith

/** @author Kotcrab */
@RunWith(GdxTestRunner::class)
class VisTableWidgetFactoryTest : TableWidgetFactoryTest(::table)

@RunWith(GdxTestRunner::class)
class ButtonWidgetFactoryTest : TableWidgetFactoryTest({ actor(KButton(DEFAULT_STYLE), it) })

@RunWith(GdxTestRunner::class)
class CheckBoxWidgetFactoryTest : TableWidgetFactoryTest({ actor(KVisCheckBox("", DEFAULT_STYLE), it) })

@RunWith(GdxTestRunner::class)
class RadioButtonWidgetFactoryTest : TableWidgetFactoryTest({ actor(KVisRadioButton(""), it) })

@RunWith(GdxTestRunner::class)
class TextButtonWidgetFactoryTest : TableWidgetFactoryTest({ actor(KVisTextButton("", DEFAULT_STYLE), it) })

@RunWith(GdxTestRunner::class)
class ImageButtonWidgetFactoryTest : TableWidgetFactoryTest({ actor(KVisImageButton(DEFAULT_STYLE), it) })

@RunWith(GdxTestRunner::class)
class ImageTextButtonWidgetFactoryTest : TableWidgetFactoryTest({ actor(KVisImageTextButton("", DEFAULT_STYLE), it) })

@RunWith(GdxTestRunner::class)
class TreeWidgetFactoryTest : WidgetGroupWidgetFactoryTest({ actor(KVisTree(DEFAULT_STYLE), it) })
