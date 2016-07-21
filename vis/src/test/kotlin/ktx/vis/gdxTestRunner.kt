package ktx.vis

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.kotcrab.vis.ui.VisUI
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class GdxTestRunner(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {
  private companion object {
    val gdxInitLatch = CountDownLatch(1)

    init {
      val executor = Executors.newSingleThreadExecutor()
      executor.submit { LwjglApplication(GdxTestRunnerApp(), createTestRunnerAppConfig()) }
      executor.shutdown()
    }

    private fun createTestRunnerAppConfig(): LwjglApplicationConfiguration {
      val config = LwjglApplicationConfiguration()
      config.width = 1
      config.height = 1
      config.x = -100
      config.y = -100
      return config
    }

    class GdxTestRunnerApp : ApplicationAdapter() {
      override fun create() {
        VisUI.load()
        gdxInitLatch.countDown()
      }

      override fun dispose() {
        VisUI.dispose()
      }
    }
  }

  override fun runChild(method: FrameworkMethod, notifier: RunNotifier) {
    gdxInitLatch.await()
    val runTestLatch = CountDownLatch(1)
    Gdx.app.postRunnable {
      super.runChild(method, notifier)
      runTestLatch.countDown()
    }
    runTestLatch.await()
  }
}
