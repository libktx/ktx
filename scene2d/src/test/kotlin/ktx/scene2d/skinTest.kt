package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import org.junit.Assert
import org.junit.Test

class Scene2DSkinTest {
  @Test
  fun `should invoke skin reload listeners`() {
    var invoked = false

    Scene2DSkin.addListener { invoked = true }

    Scene2DSkin.defaultSkin = Skin() // Invokes setter, should trigger listeners.
    Assert.assertTrue(invoked)
  }

  @Test
  fun `should remove listeners`() {
    var amount = 0
    val listener: (Skin) -> Unit = { amount++ }
    Scene2DSkin.addListener(listener)

    Scene2DSkin.defaultSkin = Skin() // Invokes setter, should trigger listeners.
    Assert.assertEquals(1, amount)

    Scene2DSkin.removeListener(listener)
    Scene2DSkin.defaultSkin = Skin() // Invokes setter, should trigger listeners.
    Assert.assertEquals(1, amount)
  }

  @Test
  fun `should clear listeners`() {
    var amount = 0
    Scene2DSkin.addListener { amount++ }
    Scene2DSkin.addListener { amount++ }

    Scene2DSkin.defaultSkin = Skin() // Invokes setter, should trigger listeners.
    Assert.assertEquals(2, amount)

    Scene2DSkin.clearListeners()
    Scene2DSkin.defaultSkin = Skin() // Invokes setter, should trigger listeners.
    Assert.assertEquals(2, amount)
  }
}
