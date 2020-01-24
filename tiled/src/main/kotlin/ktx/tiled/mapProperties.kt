package ktx.tiled

import com.badlogic.gdx.maps.MapProperties

/**
 * Allows to check if a [MapProperties] instance contains a property mapped to [key]
 * with Kotlin `in` operator.
 * @param key name of the property.
 * @return true if the property with the given [key] can be found in this [MapProperties]. False otherwise.
 */
operator fun MapProperties.contains(key: String): Boolean = containsKey(key)

/**
 * Allows to add a property mapped to [key] with given [value] using Kotlin
 * square braces assignment operator.
 * @param key name of the property.
 * @param value value of the property.
 */
operator fun MapProperties.set(key: String, value: Any) = put(key, value)
