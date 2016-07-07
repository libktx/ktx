@file:Suppress("NOTHING_TO_INLINE")

package ktx.collections

import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.ObjectMap.Entry

/**
 * Default LibGDX map size used by most constructors.
 */
const val defaultMapSize = 51

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [ObjectMap].
 */
fun <Key, Value> gdxMapOf(initialCapacity: Int = defaultSetSize, loadFactor: Float = defaultLoadFactor): ObjectMap<Key, Value> =
    ObjectMap(initialCapacity, loadFactor)

/**
 * @param keysToValues will be added to the map.
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [ObjectMap].
 */
inline fun <Key, Value> gdxMapOf(vararg keysToValues: Pair<Key, Value>,
                                 initialCapacity: Int = defaultSetSize,
                                 loadFactor: Float = defaultLoadFactor): ObjectMap<Key, Value> {
  val map = ObjectMap<Key, Value>(initialCapacity, loadFactor)
  keysToValues.forEach { map[it.first] = it.second }
  return map
}

/**
 * A method wrapper over [ObjectMap.size] variable compatible with nullable types.
 * @return current amount of elements in the map.
 */
inline fun ObjectMap<*, *>?.size(): Int = this?.size ?: 0

/**
 * @return true if the map is null or has no elements.
 */
inline fun ObjectMap<*, *>?.isEmpty(): Boolean = this == null || this.size == 0

/**
 * @return true if the map is not null and contains at least one element.
 */
inline fun ObjectMap<*, *>?.isNotEmpty(): Boolean = this != null && this.size > 0

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return true if a value is associated with passed key. False otherwise.
 */
operator fun <Key> ObjectMap<Key, *>.contains(key: Key): Boolean = this.containsKey(key)

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 * @return old value associated with the key or null if none.
 */
operator fun <Key, Value> ObjectMap<Key, Value>.set(key: Key, value: Value): Value? = this.put(key, value)

/**
 * Allows to iterate over the map with Kotlin lambda syntax and direct access to [MutableIterator], which can remove
 * elements during iteration.
 * @param action will be invoked on each key and value pair. Passed iterator is ensured to be the same instance throughout
 *    the iteration. It can be used to remove elements.
 */
inline fun <Key, Value> ObjectMap<Key, Value>.iterate(action: (Key, Value, MutableIterator<Entry<Key, Value>>) -> Unit) {
  val iterator = this.iterator()
  while (iterator.hasNext) {
    val next = iterator.next()
    action(next.key, next.value, iterator)
  }
}

/**
 * @return keys from this map stored in an [ObjectSet].
 */
fun <Key> ObjectMap<Key, *>.toGdxSet(): ObjectSet<Key> = this.keys().toGdxSet()

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides how many elements the map might contain in relation to its total capacity before it is resized.
 * @param keyProvider will consume each value in this iterable. The results will be treated as map keys for the values.
 * @return values copied from this iterable stored in a LibGDX map, mapped to the keys returned by the provider.
 */
inline fun <Key, Value> Iterable<Value>.toGdxMap(initialCapacity: Int = defaultMapSize, loadFactor: Float = defaultLoadFactor,
                                                 keyProvider: (Value) -> Key): ObjectMap<Key, Value> {
  val map = ObjectMap<Key, Value>(initialCapacity, loadFactor)
  this.forEach { map[keyProvider(it)] = it }
  return map
}

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides how many elements the map might contain in relation to its total capacity before it is resized.
 * @param valueProvider will consume each value in this iterable. The results will be treated as map values.
 * @param keyProvider will consume each value in this iterable. The results will be treated as map keys for the values.
 * @return values converted from this iterable stored in a LibGDX map, mapped to the keys returned by the provider.
 */
inline fun <Type, Key, Value> Iterable<Type>.toGdxMap(initialCapacity: Int = defaultMapSize,
                                                      loadFactor: Float = defaultLoadFactor,
                                                      valueProvider: (Type) -> Value,
                                                      keyProvider: (Type) -> Key): ObjectMap<Key, Value> {
  val map = ObjectMap<Key, Value>(initialCapacity, loadFactor)
  this.forEach { map[keyProvider(it)] = valueProvider(it) }
  return map
}

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides how many elements the map might contain in relation to its total capacity before it is resized.
 * @param keyProvider will consume each value in this iterable. The results will be treated as map keys for the values.
 * @return values copied from this array stored in a LibGDX map, mapped to the keys returned by the provider.
 */
inline fun <Key, Value> Array<Value>.toGdxMap(initialCapacity: Int = defaultMapSize, loadFactor: Float = defaultLoadFactor,
                                              keyProvider: (Value) -> Key): ObjectMap<Key, Value> {
  val map = ObjectMap<Key, Value>(initialCapacity, loadFactor)
  this.forEach { map[keyProvider(it)] = it }
  return map
}

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides how many elements the map might contain in relation to its total capacity before it is resized.
 * @param valueProvider will consume each value in this iterable. The results will be treated as map values.
 * @param keyProvider will consume each value in this iterable. The results will be treated as map keys for the values.
 * @return values converted from this array stored in a LibGDX map, mapped to the keys returned by the provider.
 */
inline fun <Type, Key, Value> Array<Type>.toGdxMap(initialCapacity: Int = defaultMapSize,
                                                   loadFactor: Float = defaultLoadFactor,
                                                   valueProvider: (Type) -> Value,
                                                   keyProvider: (Type) -> Key): ObjectMap<Key, Value> {
  val map = ObjectMap<Key, Value>(initialCapacity, loadFactor)
  this.forEach { map[keyProvider(it)] = valueProvider(it) }
  return map
}

// Sadly, IdentityMap does NOT extend ObjectMap. It feels like it would require up to 2 overridden methods to set it up,
// but NO. The utilities that apply to ObjectMaps will not work on IdentityMaps, and since its a lot of extra work (and
// boilerplate) for such a pretty obscure class, only some basic functions are added - like factory methods and operators.
/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [IdentityMap], which compares keys by references.
 */
fun <Key, Value> gdxIdentityMapOf(initialCapacity: Int = defaultSetSize, loadFactor: Float = defaultLoadFactor):
    IdentityMap<Key, Value> = IdentityMap(initialCapacity, loadFactor)

/**
 * @param keysToValues will be added to the map.
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [IdentityMap], which compares keys by references.
 */
inline fun <Key, Value> gdxIdentityMapOf(vararg keysToValues: Pair<Key, Value>,
                                         initialCapacity: Int = defaultSetSize,
                                         loadFactor: Float = defaultLoadFactor): IdentityMap<Key, Value> {
  val map = IdentityMap<Key, Value>(initialCapacity, loadFactor)
  keysToValues.forEach { map[it.first] = it.second }
  return map
}

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return true if a value is associated with passed key. False otherwise.
 */
operator fun <Key> IdentityMap<Key, *>.contains(key: Key): Boolean = this.containsKey(key)

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 * @return old value associated with the key or null if none.
 */
operator fun <Key, Value> IdentityMap<Key, Value>.set(key: Key, value: Value): Value? = this.put(key, value)

/**
 * Allows to iterate over the map with Kotlin lambda syntax and direct access to [MutableIterator], which can remove elements
 * during iteration.
 * @param action will be invoked on each key and value pair. Passed iterator is ensured to be the same instance throughout
 *    the iteration. It can be used to remove elements.
 */
inline fun <Key, Value> IdentityMap<Key, Value>.iterate(action: (Key, Value, MutableIterator<IdentityMap.Entry<Key, Value>>) -> Unit) {
  val iterator = this.iterator()
  while (iterator.hasNext()) {
    val next = iterator.next()
    action(next.key, next.value, iterator)
  }
}

// Some basic support is also provided for optimized LibGDX maps with primitive keys. Again, no common superclass hurts.

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [IntIntMap] with primitive int keys and values.
 */
fun gdxIntIntMap(initialCapacity: Int = defaultSetSize, loadFactor: Float = defaultLoadFactor): IntIntMap
    = IntIntMap(initialCapacity, loadFactor)

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return true if a value is associated with passed key. False otherwise.
 */
operator fun IntIntMap.contains(key: Int): Boolean = this.containsKey(key)

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 */
operator fun IntIntMap.set(key: Int, value: Int) = this.put(key, value)

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return value associated with this key if present in this map, 0f otherwise. Use [IntIntMap.get] with second argument
 *    (default value) if you want predictable behavior in case of missing keys.
 * @see IntIntMap.get
 */
operator fun IntIntMap.get(key: Int): Int = this.get(key, 0)

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [IntFloatMap] with primitive int keys and primitive float values.
 */
fun gdxIntFloatMap(initialCapacity: Int = defaultSetSize, loadFactor: Float = defaultLoadFactor): IntFloatMap
    = IntFloatMap(initialCapacity, loadFactor)

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return true if a value is associated with passed key. False otherwise.
 */
operator fun IntFloatMap.contains(key: Int): Boolean = this.containsKey(key)

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 */
operator fun IntFloatMap.set(key: Int, value: Float) = this.put(key, value)

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return value associated with this key if present in this map, 0f otherwise. Use [IntFloatMap.get] with second argument
 *    (default value) if you want predictable behavior in case of missing keys.
 * @see IntFloatMap.get
 */
operator fun IntFloatMap.get(key: Int): Float = this.get(key, 0f)

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [IntMap] with primitive int keys.
 */
fun <Value> gdxIntMap(initialCapacity: Int = defaultSetSize, loadFactor: Float = defaultLoadFactor): IntMap<Value>
    = IntMap(initialCapacity, loadFactor)

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return true if a value is associated with passed key. False otherwise.
 */
operator fun IntMap<*>.contains(key: Int): Boolean = this.containsKey(key)

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 * @return old value associated with the key or null if none.
 */
operator fun <Value> IntMap<Value>.set(key: Int, value: Value): Value? = this.put(key, value)
