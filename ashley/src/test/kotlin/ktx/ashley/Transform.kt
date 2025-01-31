package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

class Transform(
  var x: Float = 0f,
  var y: Float = 0f,
) : Component {
  companion object {
    val mapper = ComponentMapper.getFor(Transform::class.java)!!
  }
}
