package ktx.ai

import com.badlogic.gdx.ai.btree.LeafTask
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute

class EatTask(
  @JvmField
  @TaskAttribute(required = true)
  var hunger: Int,
) : LeafTask<Cat>() {
  override fun copyTo(task: Task<Cat>): Task<Cat> = this

  override fun execute(): Status = Status.SUCCEEDED
}
