package ktx.json

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue

/**
 * Wrapping interface around [com.badlogic.gdx.utils.Json.Serializer].
 * Improves typing by adding nullability information and changes default parameter names.
 */
interface JsonSerializer<T> : Json.Serializer<T> {
  override fun read(
    json: Json,
    jsonValue: JsonValue,
    type: Class<*>?,
  ): T

  override fun write(
    json: Json,
    value: T,
    type: Class<*>?,
  )
}

/**
 * Wrapping interface around [com.badlogic.gdx.utils.Json.Serializer]. Provides null-safety
 * and convenient interface for serializer that is only able to [read].
 * Unlike libGDX [com.badlogic.gdx.utils.Json.ReadOnlySerializer], the default implementation of
 * the [write] method throws [UnsupportedOperationException].
 */
interface ReadOnlyJsonSerializer<T> : JsonSerializer<T> {
  override fun write(
    json: Json,
    value: T,
    type: Class<*>?,
  ) = throw UnsupportedOperationException("Read-only serializers do not support write method.")
}

/**
 * Factory function to create a [ReadOnlyJsonSerializer] from lambda.
 */
inline fun <T> readOnlySerializer(crossinline reader: (Json, JsonValue, Class<*>?) -> T): Json.Serializer<T> =
  object : ReadOnlyJsonSerializer<T> {
    override fun read(
      json: Json,
      jsonValue: JsonValue,
      type: Class<*>?,
    ): T = reader(json, jsonValue, type)
  }

/**
 * Factory function to create a simplified [ReadOnlyJsonSerializer], which accepts only [JsonValue].
 */
inline fun <T> readOnlySerializer(crossinline read: (JsonValue) -> T): Json.Serializer<T> =
  object : ReadOnlyJsonSerializer<T> {
    override fun read(
      json: Json,
      jsonValue: JsonValue,
      type: Class<*>?,
    ): T = read(jsonValue)
  }
