package ktx.json

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue

/**
 * Wrapping interface around [com.badlogic.gdx.utils.Json.Serializer].
 * Provides null-safety for the methods.
 */
interface JsonSerializer<T> : Json.Serializer<T> {
  override fun write(json: Json, obj: T, knownType: Class<*>?)
  override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): T
}

/**
 * Wrapping interface around [com.badlogic.gdx.utils.Json.Serializer]. Provides null-safety
 * and convenient interface for serializer that is only able to [read].
 * Unlike LibGDX [ReadOnlySerializer][com.badlogic.gdx.utils.Json.ReadOnlySerializer], the [write]
 * method throws [UnsupportedOperationException]
 */
interface ReadOnlyJsonSerializer<T> : Json.Serializer<T> {
  override fun write(json: Json, obj: T, knownType: Class<*>?) = throw UnsupportedOperationException("Read-only serializer does not support writing")
  override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): T
}

/**
 * Factory function to create a [ReadOnlyJsonSerializer] from lambda
 */
inline fun <T> readOnlySerializer(crossinline reader: (Json, JsonValue, Class<*>?) -> T): Json.Serializer<T> =
    object : ReadOnlyJsonSerializer<T> {
      override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): T = reader(json, jsonData, type)
    }

/**
 * Factory function to create a simplified [ReadOnlyJsonSerializer], which accepts only [JsonValue].
 */
inline fun <T> readOnlySerializer(crossinline read: (JsonValue) -> T): Json.Serializer<T> =
    object : ReadOnlyJsonSerializer<T> {
      override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): T = read(jsonData)
    }
