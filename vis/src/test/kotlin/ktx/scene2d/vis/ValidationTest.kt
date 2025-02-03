package ktx.scene2d.vis

import ktx.scene2d.ApplicationTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationTest : ApplicationTest() {
  @Test
  fun `should create FormValidator`() {
    var invoked: Boolean

    val formValidator =
      validator {
        invoked = true
      }

    assertNotNull(formValidator)
    assertTrue(invoked)
  }
}
