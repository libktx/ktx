package ktx.style

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito

/**
 * Tests [Skin] building utilities.
 * @author MJ
 */
class StyleTest {
  @Test
  fun shouldUseValidDefaultStyle() {
    val skin = Skin()
    skin.add(defaultStyle, "Mock resource.")
    // Calling getter without resource name - should default to "default":
    val resource = skin[String::class.java]
    assertNotNull(resource)
    assertEquals("Mock resource.", resource)
    // If this test fails, LibGDX changed name of default resources or removed the unnamed resource getter.
  }

  @Test
  fun shouldCreateNewSkinWithInitBlock() {
    val skin = skin {
      add("mock", "Test.")
    }
    assertNotNull(skin)
    assertNotNull(skin.get("mock", String::class.java))
  }

  @Test
  fun shouldCreateNewSkinWithTextureAtlasAndInitBlock() {
    val skin = skin(TextureAtlas()) {
      add("mock", "Test.")
    }
    assertNotNull(skin)
    assertNotNull(skin.get("mock", String::class.java))
  }

  @Test
  fun shouldExtractResourceWithReifiedType() {
    val skin = Skin()
    skin.add("mock", "Test.")
    val resource = skin.get<String>("mock")
    assertNotNull(resource)
    assertEquals("Test.", resource)

    val reified: String = skin.get("mock")
    assertNotNull(reified)
    assertEquals("Test.", reified)

    val infix: String = skin get "mock"
    assertNotNull(infix)
    assertEquals("Test.", infix)

    val operator: String = skin["mock"]
    assertNotNull(operator)
    assertEquals("Test.", operator)
  }

  @Test
  fun shouldAddResourcesWithBraceOperator() {
    val skin = Skin()
    skin["name"] = "Asset."
    val asset: String = skin["name"]
    assertNotNull(asset)
    assertEquals("Asset.", asset)
  }

  @Test
  fun shouldAddExistingStyleToSkin() {
    val skin = Skin()
    val existing = LabelStyle()
    skin.addStyle("mock", existing) {
      fontColor = Color.BLACK
    }
    val style = skin.get<LabelStyle>("mock")
    assertSame(existing, style)
    assertEquals(Color.BLACK, style.fontColor)
  }

  @Test
  fun shouldAddColor() {
    val skin = Skin()
    skin.color("black", red = 0f, green = 0f, blue = 0f)
    val color = skin.getColor("black")
    assertEquals(Color.BLACK, color)
  }

  @Test
  fun shouldAddButtonStyle() {
    val skin = skin {
      button {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<ButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun shouldExtendButtonStyle() {
    val skin = skin {
      button("base") {
        pressedOffsetX = 1f
      }
      button("new", extend = "base") {
        pressedOffsetY = 1f
      }
    }
    val style = skin.get<ButtonStyle>("new")
    assertEquals(1f, style.pressedOffsetX)
    assertEquals(1f, style.pressedOffsetY)
  }

  @Test
  fun shouldAddCheckBoxStyle() {
    val skin = skin {
      checkBox {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<CheckBoxStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test // TODO Include in test suite once the constructor is fixed.
  @Ignore("CheckBoxStyle copy constructor is broken in LibGDX 1.9.3 and does not copy every property.")
  fun shouldExtendCheckBoxStyle() {
    val skin = skin {
      checkBox("base") {
        pressedOffsetX = 1f
      }
      checkBox("new", extend = "base") {
        pressedOffsetY = 1f
      }
    }
    val style = skin.get<CheckBoxStyle>("new")
    assertEquals(1f, style.pressedOffsetX)
    assertEquals(1f, style.pressedOffsetY)
  }

  @Test
  fun shouldAddImageButtonStyle() {
    val skin = skin {
      imageButton {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<ImageButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun shouldExtendImageButtonStyle() {
    val skin = skin {
      imageButton("base") {
        pressedOffsetX = 1f
      }
      imageButton("new", extend = "base") {
        pressedOffsetY = 1f
      }
    }
    val style = skin.get<ImageButtonStyle>("new")
    assertEquals(1f, style.pressedOffsetX)
    assertEquals(1f, style.pressedOffsetY)
  }

  @Test
  fun shouldAddImageTextButtonStyle() {
    val skin = skin {
      imageTextButton {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<ImageTextButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun shouldExtendImageTextButtonStyle() {
    val skin = skin {
      imageTextButton("base") {
        pressedOffsetX = 1f
      }
      imageTextButton("new", extend = "base") {
        pressedOffsetY = 1f
      }
    }
    val style = skin.get<ImageTextButtonStyle>("new")
    assertEquals(1f, style.pressedOffsetX)
    assertEquals(1f, style.pressedOffsetY)
  }

  @Test
  fun shouldAddLabelStyle() {
    val skin = skin {
      label {
        fontColor = Color.BLACK
      }
    }
    val style = skin.get<LabelStyle>(defaultStyle)
    assertEquals(Color.BLACK, style.fontColor)
  }

  @Test
  fun shouldExtendLabelStyle() {
    val skin = skin {
      label("base") {
        fontColor = Color.BLACK
      }
      label("new", extend = "base") {
      }
    }
    val style = skin.get<LabelStyle>("new")
    assertEquals(Color.BLACK, style.fontColor)
  }

  @Test
  fun shouldAddListStyle() {
    val skin = skin {
      list {
        this.fontColorSelected = Color.BLACK
      }
    }
    val style = skin.get<ListStyle>(defaultStyle)
    assertEquals(Color.BLACK, style.fontColorSelected)
  }

  @Test
  fun shouldExtendListStyle() {
    val skin = skin {
      list("base") {
        fontColorSelected = Color.BLACK
      }
      list("new", extend = "base") {
        fontColorUnselected = Color.CYAN
      }
    }
    val style = skin.get<ListStyle>("new")
    assertEquals(Color.BLACK, style.fontColorSelected)
    assertEquals(Color.CYAN, style.fontColorUnselected)
  }

  @Test
  fun shouldAddProgressBarStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      progressBar {
        background = drawable
      }
    }
    val style = skin.get<ProgressBarStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendProgressBarStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      progressBar("base") {
        background = drawable
      }
      progressBar("new", extend = "base") {
        knob = drawable
      }
    }
    val style = skin.get<ProgressBarStyle>("new")
    assertEquals(drawable, style.background)
    assertEquals(drawable, style.knob)
  }

  @Test
  fun shouldAddScrollPaneStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      scrollPane {
        this.background = drawable
      }
    }
    val style: ScrollPaneStyle = skin get defaultStyle
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendScrollPaneStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      scrollPane("base") {
        background = drawable
      }
      scrollPane("new", extend = "base") {
        corner = drawable
      }
    }
    val style = skin.get<ScrollPaneStyle>("new")
    assertEquals(drawable, style.background)
    assertEquals(drawable, style.corner)
  }

  @Test
  fun shouldAddSelectBoxStyle() {
    val skin = skin {
      selectBox {
        fontColor = Color.CYAN
      }
    }
    val style = skin.get<SelectBoxStyle>(defaultStyle)
    assertEquals(Color.CYAN, style.fontColor)
  }

  @Test
  fun shouldExtendSelectBoxStyle() {
    val skin = skin {
      list {} // Necessary for copy constructor.
      scrollPane {}
      selectBox("base") {
        listStyle = get(defaultStyle)
        scrollStyle = get(defaultStyle)
        fontColor = Color.CYAN
      }
      selectBox("new", extend = "base") {
        disabledFontColor = Color.BLACK
      }
    }
    val style = skin.get<SelectBoxStyle>("new")
    assertEquals(Color.CYAN, style.fontColor)
    assertEquals(Color.BLACK, style.disabledFontColor)
  }

  @Test
  fun shouldAddSliderStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      slider {
        background = drawable
      }
    }
    val style = skin.get<SliderStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendSliderStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      slider("base") {
        background = drawable
      }
      slider("new", extend = "base") {
        knob = drawable
      }
    }
    val style = skin.get<SliderStyle>("new")
    assertEquals(drawable, style.background)
    assertEquals(drawable, style.knob)
  }

  @Test
  fun shouldAddSplitPaneStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      splitPane {
        handle = drawable
      }
    }
    val style = skin.get<SplitPaneStyle>(defaultStyle)
    assertEquals(drawable, style.handle)
  }

  @Test
  fun shouldExtendSplitPaneStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      splitPane("base") {
        handle = drawable
      }
      splitPane("new", extend = "base") {
      }
    }
    val style = skin.get<SplitPaneStyle>("new")
    assertEquals(drawable, style.handle)
  }

  @Test
  fun shouldAddTextButtonStyle() {
    val skin = skin {
      textButton {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<TextButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun shouldExtendTextButtonStyle() {
    val skin = skin {
      textButton("base") {
        pressedOffsetX = 1f
      }
      textButton("new", extend = "base") {
        pressedOffsetY = 1f
      }
    }
    val style = skin.get<TextButtonStyle>("new")
    assertEquals(1f, style.pressedOffsetX)
    assertEquals(1f, style.pressedOffsetY)
  }

  @Test
  fun shouldAddTextFieldStyle() {
    val skin = skin {
      textField {
        fontColor = Color.CYAN
      }
    }
    val style = skin.get<TextFieldStyle>(defaultStyle)
    assertEquals(Color.CYAN, style.fontColor)
  }

  @Test
  fun shouldExtendTextFieldStyle() {
    val skin = skin {
      textField("base") {
        fontColor = Color.CYAN
      }
      textField("new", extend = "base") {
        disabledFontColor = Color.BLACK
      }
    }
    val style = skin.get<TextFieldStyle>("new")
    assertEquals(Color.CYAN, style.fontColor)
    assertEquals(Color.BLACK, style.disabledFontColor)
  }

  @Test
  fun shouldAddTextTooltipStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      textTooltip {
        background = drawable
      }
    }
    val style = skin.get<TextTooltipStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendTextTooltipStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      textTooltip("base") {
        label = label {} // Necessary for copy constructor.
        background = drawable
      }
      textTooltip("new", extend = "base") {
        wrapWidth = 1f
      }
    }
    val style = skin.get<TextTooltipStyle>("new")
    assertEquals(drawable, style.background)
    assertEquals(1f, style.wrapWidth)
  }

  @Test
  fun shouldAddTouchpadStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      touchpad {
        knob = drawable
      }
    }
    val style = skin.get<TouchpadStyle>(defaultStyle)
    assertEquals(drawable, style.knob)
  }

  @Test
  fun shouldExtendTouchpadStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      touchpad("base") {
        knob = drawable
      }
      touchpad("new", extend = "base") {
        background = drawable
      }
    }
    val style = skin.get<TouchpadStyle>("new")
    assertEquals(drawable, style.knob)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldAddTreeStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      tree {
        plus = drawable
      }
    }
    val style = skin.get<TreeStyle>(defaultStyle)
    assertEquals(drawable, style.plus)
  }

  @Test
  fun shouldExtendTreeStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      tree("base") {
        plus = drawable
      }
      tree("new", extend = "base") {
        minus = drawable
      }
    }
    val style = skin.get<TreeStyle>("new")
    assertEquals(drawable, style.plus)
    assertEquals(drawable, style.minus)
  }

  @Test
  fun shouldAddWindowStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      window {
        background = drawable
      }
    }
    val style = skin.get<WindowStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun shouldExtendWindowStyle() {
    val drawable = Mockito.mock(Drawable::class.java)
    val skin = skin {
      window("base") {
        background = drawable
      }
      window("new", extend = "base") {
        stageBackground = drawable
      }
    }
    val style = skin.get<WindowStyle>("new")
    assertEquals(drawable, style.background)
    assertEquals(drawable, style.stageBackground)
  }
}
