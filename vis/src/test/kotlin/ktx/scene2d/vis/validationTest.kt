package ktx.scene2d.vis

import ktx.scene2d.NeedsLibGDX
import org.junit.Assert.*
import org.junit.Test

class ValidationTest : NeedsLibGDX() {
  @Test
  fun `should create FormValidator`() {
    var invoked: Boolean

    val formValidator = validator {
      invoked = true
    }

    assertNotNull(formValidator)
    assertTrue(invoked)
  }
}
