package ktx.vis

import com.badlogic.gdx.scenes.scene2d.Actor
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/** @author Kotcrab */

@RunWith(GdxTestRunner::class)
class BuilderTest {
  @Test
  fun shouldInvokeActorInitializer() {
    var initInvoked = false
    actor(Actor()) {
      initInvoked = true
    }
    Assert.assertTrue(initInvoked)
  }
}

@RunWith(GdxTestRunner::class)
class VerticalGroupWidgetFactoryTest() : WidgetGroupWidgetFactoryTest(::verticalGroup)

@RunWith(GdxTestRunner::class)
class HorizontalGroupWidgetFactoryTest() : WidgetGroupWidgetFactoryTest(::horizontalGroup)

@RunWith(GdxTestRunner::class)
class VerticalFlowGroupWidgetFactoryTest() : WidgetGroupWidgetFactoryTest({ verticalFlowGroup(0f, it) })

@RunWith(GdxTestRunner::class)
class HorizontalFlowGroupWidgetFactoryTest() : WidgetGroupWidgetFactoryTest({ horizontalFlowGroup(0f, it) })

@RunWith(GdxTestRunner::class)
class GridGroupWidgetFactoryTest() : WidgetGroupWidgetFactoryTest({ gridGroup(1f, 1f, it) })

@RunWith(GdxTestRunner::class)
class FloatingGroupWidgetFactoryTest() : WidgetGroupWidgetFactoryTest(::floatingGroup)

@RunWith(GdxTestRunner::class)
class FloatingGroupWidgetFactoryTestWithPrefSize() : WidgetGroupWidgetFactoryTest({ floatingGroup(1f, 1f, it) })

@RunWith(GdxTestRunner::class)
class StackWidgetFactoryTest() : WidgetGroupWidgetFactoryTest(::stack)
