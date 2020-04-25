package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A dummy widget that allows to add actors directly to the [stage] with Scene2D DSL.
 */
@Scene2dDsl
class StageWidget(val stage: Stage) : RootWidget {
  override fun <T : Actor> storeActor(actor: T): T {
    stage.addActor(actor)
    return actor
  }
}

/**
 * Allows to create and add [actors][Actor] directly to this [Stage].
 * @param init inlined. All defined top-level widgets will be added to this [Stage].
 */
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun Stage.actors(init: (@Scene2dDsl StageWidget).() -> Unit) {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  StageWidget(this).init()
}
