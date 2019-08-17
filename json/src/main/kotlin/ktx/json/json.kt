package ktx.json

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue

/**
 * Parse an object of type [T] from a [file].
 */
inline fun <reified T> Json.fromJson(file: FileHandle): T = fromJson(T::class.java, file)

/**
 * Parse an object of type [T] from a [string].
 */
inline fun <reified T> Json.fromJson(string: String): T = fromJson(T::class.java, string)

/**
 * Sets a [tag] to use instead of the fully qualifier class name for type [T].
 * This can make the JSON easier to read.
 */
inline fun <reified T> Json.addClassTag(tag: String) = addClassTag(tag, T::class.java)

/**
 * Returns the tag for type [T], or null if none was defined.
 */
inline fun <reified T> Json.getTag(): String? = getTag(T::class.java)

/**
 * Sets the elements in a collection of type [T] to type [E].
 * When the element type is known, the class for each element in the collection
 * does not need to be written unless different from the element type.
 */
inline fun <reified T, reified E> Json.setElementType(fieldName: String) =
    setElementType(T::class.java, fieldName, E::class.java)

/**
 * Registers a [serializer] to use for type [T] instead of the default behavior of
 * serializing all of an object fields.
 */
inline fun <reified T> Json.setSerializer(serializer: Json.Serializer<T>) = setSerializer(T::class.java, serializer)

/**
 * Read a value of type [T] from a [jsonData] attribute with a [name].
 * If [name] is `null`, this will directly read [jsonData] as an object of type [T].
 */
inline fun <reified T> Json.readValue(jsonData: JsonValue, name: String? = null): T =
    readValue(T::class.java, if (name == null) jsonData else jsonData.get(name))

/**
 * Read an iterable value of type [T] with elements of type [E] from a [jsonData] attribute with a [name].
 * If [name] is `null`, this will directly read [jsonData] as an iterable of type [T].
 */
inline fun <reified T : Iterable<E>, reified E> Json.readArrayValue(jsonData: JsonValue, name: String? = null): T =
    readValue(T::class.java, E::class.java, if (name == null) jsonData else jsonData.get(name))
