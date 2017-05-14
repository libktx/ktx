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
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.mock.mock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * Tests [Skin] building utilities.
 */
class StyleTest {
  @Test
  fun `should use valid default style`() {
    val skin = Skin()
    skin.add(defaultStyle, "Mock resource.")

    // Calling getter without resource name - should default to "default":
    val resource = skin[String::class.java]

    resource shouldNotBe null
    resource shouldBe "Mock resource."
    // If this test fails, LibGDX changed name of default resources or removed the unnamed resource getter.
  }

  @Test
  fun `should create new skin with init block`() {
    val skin = skin {
      add("mock", "Test.")
    }

    skin shouldNotBe null

    skin.get("mock", String::class.java) shouldNotBe null
  }

  @Test
  fun `should create new skin with TextureAtlas and init block`() {
    val skin = skin(TextureAtlas()) {
      add("mock", "Test.")
    }

    skin shouldNotBe null

    skin.get("mock", String::class.java) shouldNotBe null
  }

  @Test
  fun `should extract resource with reified type`() {
    val skin = Skin()
    skin.add("mock", "Test.")

    val resource = skin.get<String>("mock")
    resource shouldBe "Test."

    @Suppress("ReplaceGetOrSet")
    val reified: String = skin.get("mock")
    reified shouldBe "Test."

    val infix: String = skin get "mock"
    infix shouldBe "Test."

    val operator: String = skin["mock"]
    operator shouldBe "Test."
  }

  @Test
  fun `should add resources with brace operator`() {
    val skin = Skin()

    skin["name"] = "Asset."

    skin.get<String>("name") shouldBe "Asset."
  }

  @Test
  fun `should add existing style to skin`() {
    val skin = Skin()
    val existing = LabelStyle()

    skin.addStyle("mock", existing) {
      fontColor = Color.BLACK
    }

    val style = skin.get<LabelStyle>("mock")
    assertSame(existing, style)
    style.fontColor shouldBe Color.BLACK
  }

  @Test
  fun `should add color`() {
    val skin = Skin()

    skin.color("black", red = 0f, green = 0f, blue = 0f)

    skin.getColor("black") shouldBe Color.BLACK
  }

  @Test
  fun `should add ButtonStyle`() {
    val skin = skin {
      button {
        pressedOffsetX = 1f
      }
    }
    val style = skin.get<ButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun `should extend ButtonStyle`() {
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
  fun `should add CheckBoxStyle`() {
    val skin = skin {
      checkBox {
        pressedOffsetX = 1f
      }
    }

    val style = skin.get<CheckBoxStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun `should extend CheckBoxStyle`() {
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
  fun `should add ImageButtonStyle`() {
    val skin = skin {
      imageButton {
        pressedOffsetX = 1f
      }
    }

    val style = skin.get<ImageButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun `should extend ImageButtonStyle`() {
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
  fun `should add ImageTextButtonStyle`() {
    val skin = skin {
      imageTextButton {
        pressedOffsetX = 1f
      }
    }

    val style = skin.get<ImageTextButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun `should extend ImageTextButtonStyle`() {
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
  fun `should add LabelStyle`() {
    val skin = skin {
      label {
        fontColor = Color.BLACK
      }
    }

    val style = skin.get<LabelStyle>(defaultStyle)
    assertEquals(Color.BLACK, style.fontColor)
  }

  @Test
  fun `should extend LabelStyle`() {
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
  fun `should add ListStyle`() {
    val skin = skin {
      list {
        this.fontColorSelected = Color.BLACK
      }
    }

    val style = skin.get<ListStyle>(defaultStyle)
    assertEquals(Color.BLACK, style.fontColorSelected)
  }

  @Test
  fun `should extend ListStyle`() {
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
  fun `should add ProgressBarStyle`() {
    val drawable = mock<Drawable>()
    val skin = skin {
      progressBar {
        background = drawable
      }
    }

    val style = skin.get<ProgressBarStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend ProgressBarStyle`() {
    val drawable = mock<Drawable>()
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
  fun `should add ScrollPaneStyle`() {
    val drawable = mock<Drawable>()
    val skin = skin {
      scrollPane {
        this.background = drawable
      }
    }

    val style: ScrollPaneStyle = skin get defaultStyle
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend ScrollPaneStyle`() {
    val drawable = mock<Drawable>()
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
  fun `should add SelectBoxStyle`() {
    val skin = skin {
      selectBox {
        fontColor = Color.CYAN
      }
    }

    val style = skin.get<SelectBoxStyle>(defaultStyle)
    assertEquals(Color.CYAN, style.fontColor)
  }

  @Test
  fun `should extend SelectBoxStyle`() {
    val skin = skin {
      list {} // Necessary for copy constructor.
      scrollPane {}
      selectBox("base") {
        listStyle = it[defaultStyle]
        scrollStyle = it[defaultStyle]
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
  fun `should add SliderStyle`() {
    val drawable = mock<Drawable>()
    val skin = skin {
      slider {
        background = drawable
      }
    }

    val style = skin.get<SliderStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend SliderStyle`() {
    val drawable = mock<Drawable>()
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
  fun `should add SplitPaneStyle`() {
    val drawable = mock<Drawable>()
    val skin = skin {
      splitPane {
        handle = drawable
      }
    }

    val style = skin.get<SplitPaneStyle>(defaultStyle)
    assertEquals(drawable, style.handle)
  }

  @Test
  fun `should extend SplitPaneStyle`() {
    val drawable = mock<Drawable>()
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
  fun `should add TextButtonStyle`() {
    val skin = skin {
      textButton {
        pressedOffsetX = 1f
      }
    }

    val style = skin.get<TextButtonStyle>(defaultStyle)
    assertEquals(1f, style.pressedOffsetX)
  }

  @Test
  fun `should extend TextButtonStyle`() {
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
  fun `should add TextFieldStyle`() {
    val skin = skin {
      textField {
        fontColor = Color.CYAN
      }
    }

    val style = skin.get<TextFieldStyle>(defaultStyle)
    assertEquals(Color.CYAN, style.fontColor)
  }

  @Test
  fun `should extend TextFieldStyle`() {
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
  fun `should add TextTooltipStyle`() {
    val drawable = mock<Drawable>()
    val skin = skin {
      textTooltip {
        background = drawable
      }
    }

    val style = skin.get<TextTooltipStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend TextTooltipStyle`() {
    val drawable = mock<Drawable>()
    val skin = skin {
      textTooltip("base") {
        label = it.label {} // Necessary for copy constructor.
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
  fun `should add TouchpadStyle`() {
    val drawable = mock<Drawable>()
    val skin = skin {
      touchpad {
        knob = drawable
      }
    }

    val style = skin.get<TouchpadStyle>(defaultStyle)
    assertEquals(drawable, style.knob)
  }

  @Test
  fun `should extend TouchpadStyle`() {
    val drawable = mock<Drawable>()
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
  fun `should add TreeStyle`() {
    val drawable = mock<Drawable>()
    val skin = skin {
      tree {
        plus = drawable
      }
    }

    val style = skin.get<TreeStyle>(defaultStyle)
    assertEquals(drawable, style.plus)
  }

  @Test
  fun `should extend TreeStyle`() {
    val drawable = mock<Drawable>()
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
  fun `should add WindowStyle`() {
    val drawable = mock<Drawable>()
    val skin = skin {
      window {
        background = drawable
      }
    }

    val style = skin.get<WindowStyle>(defaultStyle)
    assertEquals(drawable, style.background)
  }

  @Test
  fun `should extend WindowStyle`() {
    val drawable = mock<Drawable>()
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
