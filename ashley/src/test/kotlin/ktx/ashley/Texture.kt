package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper

class Texture: Component {
  companion object {
    val mapper = ComponentMapper.getFor(Texture::class.java)!!
  }
}
